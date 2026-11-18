package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.FormId
import ru.ifmo.se.api.domain.model.ResumeId
import ru.ifmo.se.api.domain.model.VacancyId

data class CreateApplicationCommand(
    val vacancyId: VacancyId,
    val resumeId: ResumeId,
    val formId: FormId? = null,
    val inlineForm: CreateFormCommand? = null,
    val personalDataConsent: Boolean,
)

data class UpdateApplicationStatusCommand(
    val status: ApplicationStatus,
)
