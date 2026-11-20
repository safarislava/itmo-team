package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class SubmissionRequest(
    val contestId: UUID,
    val taskId: UUID,
    val languageId: UUID,
    @field:NotBlank
    @field:Size(min = 1, max = 2097152)
    val sourceCode: String,
)
