package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.annotation.ReadOnlyDataAccess
import ru.ifmo.se.api.application.annotation.WriteDataAccess
import ru.ifmo.se.api.application.dto.ApplicationDetails
import ru.ifmo.se.api.application.dto.CreateApplicationCommand
import ru.ifmo.se.api.application.dto.UpdateApplicationStatusCommand
import ru.ifmo.se.api.application.policy.ApplicationPolicy
import ru.ifmo.se.api.domain.exception.ApplicationBadRequestException
import ru.ifmo.se.api.domain.exception.ApplicationNotFoundException
import ru.ifmo.se.api.domain.exception.FormNotFoundException
import ru.ifmo.se.api.domain.exception.ResumeNotFoundException
import ru.ifmo.se.api.domain.exception.TeamNotFoundException
import ru.ifmo.se.api.domain.exception.VacancyNotFoundException
import ru.ifmo.se.api.domain.model.Application
import ru.ifmo.se.api.domain.model.ApplicationFilter
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.Form
import ru.ifmo.se.api.domain.model.FormId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.VacancyStatus
import ru.ifmo.se.api.domain.model.map
import ru.ifmo.se.api.domain.repository.ApplicationRepository
import ru.ifmo.se.api.domain.repository.FormRepository
import ru.ifmo.se.api.domain.repository.ResumeRepository
import ru.ifmo.se.api.domain.repository.TeamRepository
import ru.ifmo.se.api.domain.repository.VacancyRepository

@Service
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val vacancyRepository: VacancyRepository,
    private val teamRepository: TeamRepository,
    private val resumeRepository: ResumeRepository,
    private val formRepository: FormRepository,
    private val applicationPolicy: ApplicationPolicy,
) {
    @WriteDataAccess
    @Transactional
    fun createApplication(
        currentPerson: Person,
        command: CreateApplicationCommand,
    ): ApplicationDetails {
        applicationPolicy.checkCanCreate(currentPerson)

        val vacancy =
            vacancyRepository.findById(command.vacancyId)
                ?: throw VacancyNotFoundException()

        if (vacancy.status != VacancyStatus.OPEN) {
            throw ApplicationBadRequestException("Cannot apply to a vacancy that is not OPEN.")
        }

        val resume =
            resumeRepository.findById(command.resumeId)
                ?: throw ResumeNotFoundException()

        val formId =
            when {
                command.formId != null -> {
                    val form =
                        formRepository.findById(command.formId)
                            ?: throw FormNotFoundException()
                    form.id
                }

                command.inlineForm != null -> {
                    val newForm =
                        Form(
                            id = FormId.generate(),
                            candidateId = currentPerson.id,
                            university = command.inlineForm.university,
                            motivation = command.inlineForm.motivation,
                            petProjects = command.inlineForm.petProjects,
                            jobExp = command.inlineForm.jobExp,
                            workFormat = command.inlineForm.workFormat,
                            skillIds = command.inlineForm.skillIds,
                        )
                    formRepository.save(newForm).id
                }

                else -> {
                    throw ApplicationBadRequestException("Either formId or inlineForm must be provided.")
                }
            }

        val application =
            Application(
                id = ApplicationId.generate(),
                candidateId = currentPerson.id,
                vacancyId = command.vacancyId,
                resumeId = resume.id,
                formId = formId,
                status = ApplicationStatus.NEW,
            )

        val saved = applicationRepository.save(application)
        return assembleDetails(saved)
    }

    @ReadOnlyDataAccess
    fun getApplications(
        currentPerson: Person,
        pageQuery: PageQuery,
        filter: ApplicationFilter,
    ): PageResult<ApplicationDetails> {
        val effectiveFilter =
            applicationPolicy.applyFilterScope(currentPerson, filter)
                ?: return PageResult.empty(pageQuery.pageNumber)

        val paged = applicationRepository.findAll(effectiveFilter, pageQuery)
        return paged.map { assembleDetails(it) }
    }

    @ReadOnlyDataAccess
    fun getApplication(
        currentPerson: Person,
        applicationId: ApplicationId,
    ): ApplicationDetails {
        val application =
            applicationRepository.findById(applicationId)
                ?: throw ApplicationNotFoundException()

        val vacancy =
            vacancyRepository.findById(application.vacancyId)
                ?: throw VacancyNotFoundException()

        val team =
            teamRepository.findById(vacancy.teamId)
                ?: throw TeamNotFoundException()

        applicationPolicy.checkCanView(currentPerson, application, vacancy, team)

        return assembleDetails(application)
    }

    @WriteDataAccess
    @Transactional
    fun updateStatus(
        currentPerson: Person,
        applicationId: ApplicationId,
        command: UpdateApplicationStatusCommand,
    ) {
        val application =
            applicationRepository.findById(applicationId)
                ?: throw ApplicationNotFoundException()

        val vacancy =
            vacancyRepository.findById(application.vacancyId)
                ?: throw VacancyNotFoundException()

        val team =
            teamRepository.findById(vacancy.teamId)
                ?: throw TeamNotFoundException()

        applicationPolicy.checkCanUpdateStatus(currentPerson, vacancy, team)

        val updated = application.copy(status = command.status)
        applicationRepository.save(updated)
    }

    private fun assembleDetails(application: Application): ApplicationDetails {
        val resume =
            resumeRepository.findById(application.resumeId)
                ?: throw ResumeNotFoundException()
        val form =
            formRepository.findById(application.formId)
                ?: throw FormNotFoundException()

        return ApplicationDetails(
            application = application,
            resume = resume,
            form = form,
        )
    }
}
