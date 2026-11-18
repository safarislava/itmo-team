package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.InterviewDecision
import java.util.UUID

data class InterviewFeedbackResponse(
    val id: UUID,
    val review: String,
    val score: Short,
    val decision: InterviewDecision,
)
