package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.ApplicationStatus
import java.util.UUID

data class ApplicationResponse(
    val id: UUID,
    val candidateId: UUID,
    val vacancyId: UUID,
    val resume: ResumeResponse,
    val form: FormResponse,
    val status: ApplicationStatus,
)
