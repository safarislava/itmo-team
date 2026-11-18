package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.InterviewStatus
import java.time.Instant
import java.util.UUID

data class InterviewResponse(
    val id: UUID,
    val applicationId: UUID,
    val candidate: PersonSummaryResponse,
    val teamlead: PersonSummaryResponse,
    val vacancy: VacancySummaryResponse,
    val scheduledStart: Instant,
    val scheduledEnd: Instant,
    val status: InterviewStatus,
    val hasRecording: Boolean,
    val feedback: InterviewFeedbackResponse? = null,
)
