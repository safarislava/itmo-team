package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import ru.ifmo.se.api.domain.model.InterviewDecision

data class InterviewFeedbackRequest(
    @field:NotBlank
    val review: String,
    @field:Min(1)
    @field:Max(10)
    val score: Short,
    val decision: InterviewDecision,
)
