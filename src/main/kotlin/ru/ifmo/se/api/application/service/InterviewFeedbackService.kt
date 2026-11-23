package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.SaveInterviewFeedbackCommand
import ru.ifmo.se.api.application.policy.InterviewFeedbackPolicy
import ru.ifmo.se.api.domain.exception.ApplicationNotFoundException
import ru.ifmo.se.api.domain.exception.InterviewBadRequestException
import ru.ifmo.se.api.domain.exception.InterviewFeedbackNotFoundException
import ru.ifmo.se.api.domain.exception.InterviewNotFoundException
import ru.ifmo.se.api.domain.exception.TeamNotFoundException
import ru.ifmo.se.api.domain.exception.VacancyNotFoundException
import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.InterviewDecision
import ru.ifmo.se.api.domain.model.InterviewFeedback
import ru.ifmo.se.api.domain.model.InterviewFeedbackId
import ru.ifmo.se.api.domain.model.InterviewId
import ru.ifmo.se.api.domain.model.InterviewStatus
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.repository.ApplicationRepository
import ru.ifmo.se.api.domain.repository.InterviewFeedbackRepository
import ru.ifmo.se.api.domain.repository.InterviewRepository
import ru.ifmo.se.api.domain.repository.TeamRepository
import ru.ifmo.se.api.domain.repository.VacancyRepository

@Service
class InterviewFeedbackService(
    private val interviewRepository: InterviewRepository,
    private val interviewFeedbackRepository: InterviewFeedbackRepository,
    private val applicationRepository: ApplicationRepository,
    private val vacancyRepository: VacancyRepository,
    private val teamRepository: TeamRepository,
    private val interviewFeedbackPolicy: InterviewFeedbackPolicy,
) {
    fun getInterviewFeedback(
        currentPerson: Person,
        interviewId: InterviewId,
    ): InterviewFeedback {
        val interview =
            interviewRepository.findById(interviewId)
                ?: throw InterviewNotFoundException()

        val vacancy =
            vacancyRepository.findById(interview.vacancyId)
                ?: throw VacancyNotFoundException()

        val team =
            teamRepository.findById(vacancy.teamId)
                ?: throw TeamNotFoundException()

        interviewFeedbackPolicy.checkCanView(currentPerson, interview, vacancy, team)

        return interviewFeedbackRepository.findByInterviewId(interviewId)
            ?: throw InterviewFeedbackNotFoundException()
    }

    @Transactional
    fun saveInterviewFeedback(
        currentPerson: Person,
        interviewId: InterviewId,
        command: SaveInterviewFeedbackCommand,
    ): InterviewFeedback {
        val interview =
            interviewRepository.findById(interviewId)
                ?: throw InterviewNotFoundException()

        interviewFeedbackPolicy.checkCanSave(currentPerson, interview)

        if (interview.status != InterviewStatus.COMPLETED) {
            throw InterviewBadRequestException("Feedback can only be left for a completed interview.")
        }

        val application =
            applicationRepository.findByCandidateIdAndVacancyId(interview.candidateId, interview.vacancyId)
                ?: throw ApplicationNotFoundException()

        if (application.status == ApplicationStatus.OFFER) {
            throw InterviewBadRequestException("The application is already hired.")
        }

        val targetAppStatus =
            when (command.decision) {
                InterviewDecision.HIRE -> ApplicationStatus.OFFER
                InterviewDecision.NO_HIRE -> ApplicationStatus.REJECTED
                InterviewDecision.NEXT_STAGE -> ApplicationStatus.INTERVIEW
            }

        applicationRepository.save(application.copy(status = targetAppStatus))

        val feedbackDomain =
            InterviewFeedback(
                id = InterviewFeedbackId.generate(),
                interviewId = interviewId,
                review = command.review,
                score = command.score,
                decision = command.decision,
            )

        return interviewFeedbackRepository.save(feedbackDomain)
    }
}
