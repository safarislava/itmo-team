package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.ApplicationDetails
import ru.ifmo.se.api.application.dto.ApplicationResponseDetails
import ru.ifmo.se.api.application.dto.CreateApplicationResponseCommand
import ru.ifmo.se.api.application.dto.UpdateApplicationResponseCommand
import ru.ifmo.se.api.application.policy.ApplicationResponsePolicy
import ru.ifmo.se.api.domain.exception.ApplicationMaxResponsesReachedException
import ru.ifmo.se.api.domain.exception.ApplicationNotFoundException
import ru.ifmo.se.api.domain.exception.ApplicationNotInReviewStatusException
import ru.ifmo.se.api.domain.exception.ApplicationResponseNotFoundException
import ru.ifmo.se.api.domain.exception.PersonNotFoundException
import ru.ifmo.se.api.domain.exception.VacancyNotFoundException
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationResponse
import ru.ifmo.se.api.domain.model.ApplicationResponseId
import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.repository.ApplicationRepository
import ru.ifmo.se.api.domain.repository.ApplicationResponseRepository
import ru.ifmo.se.api.domain.repository.PersonRepository
import ru.ifmo.se.api.domain.repository.VacancyRepository

@Service
class ApplicationResponseService(
    private val applicationResponseRepository: ApplicationResponseRepository,
    private val applicationRepository: ApplicationRepository,
    private val vacancyRepository: VacancyRepository,
    private val personRepository: PersonRepository,
    private val applicationService: ApplicationService,
    private val applicationResponsePolicy: ApplicationResponsePolicy,
    private val reservationService: ApplicationReservationService,
) {
    @Transactional
    fun takeNextApplication(currentPerson: Person): ApplicationDetails? {
        val companyId = currentPerson.companyId ?: return null
        val hrId = currentPerson.id

        val nextAppId =
            applicationResponseRepository.findNextApplicationForScreening(
                companyId = companyId,
                hrId = hrId,
            ) ?: return null

        reservationService.reserve(nextAppId, hrId)
        return applicationService.getApplication(currentPerson, nextAppId)
    }

    fun getApplicationResponses(
        currentPerson: Person,
        applicationId: ApplicationId,
    ): List<ApplicationResponseDetails> {
        val application =
            applicationRepository.findById(applicationId)
                ?: throw ApplicationNotFoundException()

        val vacancy =
            vacancyRepository.findEntityById(application.vacancyId)
                ?: throw VacancyNotFoundException()

        applicationResponsePolicy.checkCanView(currentPerson, vacancy)

        val responses = applicationResponseRepository.findAllByApplicationId(applicationId)
        return responses.map { resp ->
            val hr = personRepository.findById(resp.hrId) ?: throw PersonNotFoundException()
            ApplicationResponseDetails(response = resp, hr = hr)
        }
    }

    @Transactional
    fun createApplicationResponse(
        currentPerson: Person,
        applicationId: ApplicationId,
        command: CreateApplicationResponseCommand,
    ): ApplicationResponseDetails {
        val application =
            applicationRepository.findById(applicationId)
                ?: throw ApplicationNotFoundException()

        val vacancy =
            vacancyRepository.findEntityById(application.vacancyId)
                ?: throw VacancyNotFoundException()

        applicationResponsePolicy.checkCanCreate(currentPerson, vacancy)

        if (application.status != ApplicationStatus.REVIEW) {
            throw ApplicationNotInReviewStatusException()
        }

        val currentCount = applicationResponseRepository.countByApplicationId(applicationId)
        if (currentCount >= 2) {
            throw ApplicationMaxResponsesReachedException()
        }

        val domainResponse =
            ApplicationResponse(
                id = ApplicationResponseId.generate(),
                applicationId = applicationId,
                hrId = currentPerson.id,
                motivation = command.motivation,
                softSkills = command.softSkills,
                petProjects = command.petProjects,
                jobExp = command.jobExp,
                hardSkills = command.hardSkills,
                workFormat = command.workFormat,
            )

        val saved = applicationResponseRepository.save(domainResponse)
        reservationService.release(applicationId)
        return ApplicationResponseDetails(response = saved, hr = currentPerson)
    }

    @Transactional
    fun updateApplicationResponse(
        currentPerson: Person,
        applicationId: ApplicationId,
        responseId: ApplicationResponseId,
        command: UpdateApplicationResponseCommand,
    ): ApplicationResponseDetails {
        val existing =
            applicationResponseRepository.findById(responseId)
                ?: throw ApplicationResponseNotFoundException()

        if (existing.applicationId != applicationId) {
            throw ApplicationResponseNotFoundException()
        }

        applicationResponsePolicy.checkCanModify(currentPerson, existing)

        val updated =
            existing.copy(
                motivation = command.motivation,
                softSkills = command.softSkills,
                petProjects = command.petProjects,
                jobExp = command.jobExp,
                hardSkills = command.hardSkills,
                workFormat = command.workFormat,
            )

        val saved = applicationResponseRepository.save(updated)
        val hr = personRepository.findById(saved.hrId) ?: currentPerson
        return ApplicationResponseDetails(response = saved, hr = hr)
    }

    @Transactional
    fun deleteApplicationResponse(
        currentPerson: Person,
        applicationId: ApplicationId,
        responseId: ApplicationResponseId,
    ) {
        val existing =
            applicationResponseRepository.findById(responseId)
                ?: throw ApplicationResponseNotFoundException()

        if (existing.applicationId != applicationId) {
            throw ApplicationResponseNotFoundException()
        }

        applicationResponsePolicy.checkCanModify(currentPerson, existing)

        applicationResponseRepository.delete(existing)
    }
}
