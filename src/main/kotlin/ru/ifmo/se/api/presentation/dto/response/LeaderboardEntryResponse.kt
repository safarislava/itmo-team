package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.ApplicationStatus
import java.util.UUID

data class LeaderboardEntryResponse(
    val applicationId: UUID,
    val candidate: PersonSummaryResponse,
    val resumeId: UUID,
    val formId: UUID,
    val status: ApplicationStatus,
    val score: Double,
    val scoreMetrics: ScoreMetricsResponse? = null,
)
