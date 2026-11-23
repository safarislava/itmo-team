package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.annotation.ReadOnlyDataAccess
import ru.ifmo.se.api.application.annotation.WriteDataAccess
import ru.ifmo.se.api.application.dto.CreateFormCommand
import ru.ifmo.se.api.application.dto.UpdateFormCommand
import ru.ifmo.se.api.application.policy.FormPolicy
import ru.ifmo.se.api.domain.exception.FormLockedException
import ru.ifmo.se.api.domain.exception.FormNotFoundException
import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.Form
import ru.ifmo.se.api.domain.model.FormId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.repository.ApplicationRepository
import ru.ifmo.se.api.domain.repository.FormRepository

@Service
class FormService(
    private val formRepository: FormRepository,
    private val formPolicy: FormPolicy,
    private val applicationRepository: ApplicationRepository,
) {
    @WriteDataAccess
    @Transactional
    fun createForm(
        currentPerson: Person,
        command: CreateFormCommand,
    ): Form {
        val form =
            Form(
                id = FormId.generate(),
                candidateId = currentPerson.id,
                university = command.university,
                motivation = command.motivation,
                petProjects = command.petProjects,
                jobExp = command.jobExp,
                workFormat = command.workFormat,
                skillIds = command.skillIds,
            )
        return formRepository.save(form)
    }

    @ReadOnlyDataAccess
    fun getForm(
        currentPerson: Person,
        formId: FormId,
    ): Form {
        val form = formRepository.findById(formId) ?: throw FormNotFoundException()
        formPolicy.checkCanAccess(currentPerson, form)
        return form
    }

    @WriteDataAccess
    @Transactional
    fun updateForm(
        currentPerson: Person,
        formId: FormId,
        command: UpdateFormCommand,
    ): Form {
        val existing = formRepository.findById(formId) ?: throw FormNotFoundException()
        formPolicy.checkCanUpdate(currentPerson, existing)

        val applications = applicationRepository.findAllByFormId(formId)
        val isLocked = applications.any { app -> app.status != ApplicationStatus.NEW }
        if (isLocked) {
            throw FormLockedException()
        }

        val updated =
            existing.copy(
                university = command.university,
                motivation = command.motivation,
                petProjects = command.petProjects,
                jobExp = command.jobExp,
                workFormat = command.workFormat,
                skillIds = command.skillIds,
            )
        return formRepository.save(updated)
    }
}
