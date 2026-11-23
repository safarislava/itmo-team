package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.InterviewDetails
import ru.ifmo.se.api.application.dto.ScheduleInterviewCommand
import ru.ifmo.se.api.application.dto.UpdateInterviewStatusCommand
import ru.ifmo.se.api.application.policy.InterviewPolicy
import ru.ifmo.se.api.application.port.out.EmailService
import ru.ifmo.se.api.application.port.out.FileStorage
import ru.ifmo.se.api.application.port.out.LiveCodingSessionCredentials
import ru.ifmo.se.api.application.port.out.LiveCodingSessionProvider
import ru.ifmo.se.api.domain.exception.ApplicationNotFoundException
import ru.ifmo.se.api.domain.exception.InterviewBadRequestException
import ru.ifmo.se.api.domain.exception.InterviewNotFoundException
import ru.ifmo.se.api.domain.exception.InterviewRecordingNotFoundException
import ru.ifmo.se.api.domain.exception.PersonNotFoundException
import ru.ifmo.se.api.domain.exception.TeamNotFoundException
import ru.ifmo.se.api.domain.exception.VacancyNotFoundException
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.Interview
import ru.ifmo.se.api.domain.model.InterviewFilter
import ru.ifmo.se.api.domain.model.InterviewId
import ru.ifmo.se.api.domain.model.InterviewStatus
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.repository.ApplicationRepository
import ru.ifmo.se.api.domain.repository.InterviewFeedbackRepository
import ru.ifmo.se.api.domain.repository.InterviewRepository
import ru.ifmo.se.api.domain.repository.PersonRepository
import ru.ifmo.se.api.domain.repository.TeamRepository
import ru.ifmo.se.api.domain.repository.VacancyRepository
import java.time.Instant

@Service
class InterviewService(
    private val interviewRepository: InterviewRepository,
    private val interviewFeedbackRepository: InterviewFeedbackRepository,
    private val applicationRepository: ApplicationRepository,
    private val vacancyRepository: VacancyRepository,
    private val teamRepository: TeamRepository,
    private val personRepository: PersonRepository,
    private val interviewPolicy: InterviewPolicy,
    private val emailService: EmailService,
    private val liveCodingSessionProvider: LiveCodingSessionProvider,
    private val fileStorage: FileStorage,
) {
    @Transactional
    fun scheduleInterview(
        currentPerson: Person,
        command: ScheduleInterviewCommand,
    ): InterviewDetails {
        val application =
            applicationRepository.findById(command.applicationId)
                ?: throw ApplicationNotFoundException()

        val vacancy =
            vacancyRepository.findById(application.vacancyId)
                ?: throw VacancyNotFoundException()

        interviewPolicy.checkCanSchedule(currentPerson, vacancy)

        if (!command.scheduledStart.isAfter(Instant.now())) {
            throw InterviewBadRequestException("scheduledStart must be in the future.")
        }
        if (!command.scheduledEnd.isAfter(command.scheduledStart)) {
            throw InterviewBadRequestException("scheduledEnd must be later than scheduledStart.")
        }

        if (application.status == ApplicationStatus.NEW ||
            application.status == ApplicationStatus.REJECTED ||
            application.status == ApplicationStatus.OFFER
        ) {
            throw InterviewBadRequestException("The application has not passed the screening or is already finalized.")
        }

        interviewRepository.scheduleInterview(
            applicationId = command.applicationId,
            scheduledStart = command.scheduledStart,
            scheduledEnd = command.scheduledEnd,
        )

        val interview =
            interviewRepository.findLatestScheduled(
                candidateId = application.candidateId,
                vacancyId = application.vacancyId,
                scheduledStart = command.scheduledStart,
            ) ?: throw InterviewNotFoundException("Failed to retrieve the scheduled interview.")

        val candidate =
            personRepository.findById(application.candidateId)
                ?: throw PersonNotFoundException()

        emailService.sendInterviewInvitation(
            toEmail = candidate.email,
            interviewId = interview.id.value,
            scheduledStart = interview.scheduledStart,
        )

        val teamlead =
            personRepository.findById(interview.teamleadId)
                ?: currentPerson

        return InterviewDetails(
            interview = interview,
            applicationId = application.id,
            candidate = candidate,
            teamlead = teamlead,
            vacancy = vacancy,
            feedback = null,
        )
    }

    fun getInterviews(
        currentPerson: Person,
        pageQuery: PageQuery,
        filter: InterviewFilter,
    ): PageResult<InterviewDetails> {
        val scopedFilter =
            interviewPolicy.applyFilterScope(currentPerson, filter)
                ?: return PageResult(
                    items = emptyList(),
                    totalElements = 0,
                    pageNumber = pageQuery.pageNumber,
                    totalPages = 0,
                )

        val paged = interviewRepository.findAll(scopedFilter, pageQuery)
        val items =
            paged.items.map { interview ->
                assembleDetails(currentPerson, interview)
            }

        return PageResult(
            items = items,
            totalElements = paged.totalElements,
            pageNumber = paged.pageNumber,
            totalPages = paged.totalPages,
        )
    }

    fun getInterview(
        currentPerson: Person,
        interviewId: InterviewId,
    ): InterviewDetails {
        val interview =
            interviewRepository.findById(interviewId)
                ?: throw InterviewNotFoundException()

        val vacancy =
            vacancyRepository.findById(interview.vacancyId)
                ?: throw VacancyNotFoundException()

        val team =
            teamRepository.findById(vacancy.teamId)
                ?: throw TeamNotFoundException()

        interviewPolicy.checkCanView(currentPerson, interview, vacancy, team)
        return assembleDetails(currentPerson, interview)
    }

    fun joinInterview(
        currentPerson: Person,
        interviewId: InterviewId,
    ): LiveCodingSessionCredentials {
        val interview =
            interviewRepository.findById(interviewId)
                ?: throw InterviewNotFoundException()

        interviewPolicy.checkCanJoin(currentPerson, interview)

        if (interview.status != InterviewStatus.SCHEDULED) {
            throw InterviewBadRequestException("The interview is not scheduled. Scheduled start: ${interview.scheduledStart}")
        }

        val now = Instant.now()
        if (now.isBefore(interview.scheduledStart) || now.isAfter(interview.scheduledEnd)) {
            throw InterviewBadRequestException(
                "The current time is outside the scheduled window. Scheduled start: ${interview.scheduledStart}",
            )
        }

        return liveCodingSessionProvider.createSession(interviewId, interview.scheduledEnd)
    }

    @Transactional
    fun updateInterviewStatus(
        currentPerson: Person,
        interviewId: InterviewId,
        command: UpdateInterviewStatusCommand,
    ): InterviewDetails {
        val interview =
            interviewRepository.findById(interviewId)
                ?: throw InterviewNotFoundException()

        interviewPolicy.checkCanUpdateStatus(currentPerson, interview)

        if (interview.status != InterviewStatus.SCHEDULED) {
            throw InterviewBadRequestException("The interview is not scheduled.")
        }

        if (command.status !in setOf(InterviewStatus.COMPLETED, InterviewStatus.CANCELLED)) {
            throw InterviewBadRequestException("Status must be COMPLETED or CANCELLED.")
        }

        val updatedInterview =
            interview.copy(
                status = command.status,
                videoS3key =
                    if (command.status == InterviewStatus.COMPLETED && interview.videoS3key == null) {
                        "interviews/${interview.id.value}/recording.mp4"
                    } else {
                        interview.videoS3key
                    },
            )

        val saved = interviewRepository.save(updatedInterview)
        return assembleDetails(currentPerson, saved)
    }

    fun getInterviewRecordingUrl(
        currentPerson: Person,
        interviewId: InterviewId,
    ): String {
        val interview =
            interviewRepository.findById(interviewId)
                ?: throw InterviewNotFoundException()

        val vacancy =
            vacancyRepository.findById(interview.vacancyId)
                ?: throw VacancyNotFoundException()

        val team =
            teamRepository.findById(vacancy.teamId)
                ?: throw TeamNotFoundException()

        interviewPolicy.checkCanViewRecording(currentPerson, interview, vacancy, team)

        val s3key = interview.videoS3key ?: throw InterviewRecordingNotFoundException()
        return fileStorage.getPresignedUrl(s3key)
    }

    private fun assembleDetails(
        currentPerson: Person,
        interview: Interview,
    ): InterviewDetails {
        val candidate =
            personRepository.findById(interview.candidateId)
                ?: throw PersonNotFoundException()
        val teamlead =
            personRepository.findById(interview.teamleadId)
                ?: throw PersonNotFoundException()
        val vacancy =
            vacancyRepository.findById(interview.vacancyId)
                ?: throw VacancyNotFoundException()

        val feedback =
            if (currentPerson.role == PersonRole.CANDIDATE) {
                null
            } else {
                interviewFeedbackRepository.findByInterviewId(interview.id)
            }

        val application =
            applicationRepository.findByCandidateIdAndVacancyId(
                candidateId = candidate.id,
                vacancyId = interview.vacancyId,
            )

        return InterviewDetails(
            interview = interview,
            applicationId = application?.id ?: ApplicationId.generate(),
            candidate = candidate,
            teamlead = teamlead,
            vacancy = vacancy,
            feedback = feedback,
        )
    }
}
