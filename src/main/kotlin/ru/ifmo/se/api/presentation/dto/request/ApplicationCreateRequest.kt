package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import java.util.UUID

data class ApplicationCreateRequest(
    val vacancyId: UUID,
    val resumeId: UUID,
    val formId: UUID? = null,
    @field:Valid
    val form: FormRequest? = null,
    @field:AssertTrue(message = "Consent to personal data processing is required")
    val personalDataConsent: Boolean,
)
