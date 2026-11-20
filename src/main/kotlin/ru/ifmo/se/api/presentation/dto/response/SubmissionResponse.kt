package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.SubmissionStatus
import java.time.Instant
import java.util.UUID

data class SubmissionResponse(
    val id: UUID,
    val sender: PersonSummaryResponse,
    val contestId: UUID,
    val taskId: UUID,
    val language: LanguageResponse,
    val status: SubmissionStatus,
    val attempt: Int,
    val aiScore: Int?,
    val submittedAt: Instant,
    val result: SubmissionResultResponse?,
)
