package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.LeaderboardEntry
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.presentation.dto.response.LeaderboardEntryResponse
import ru.ifmo.se.api.presentation.dto.response.PersonSummaryResponse

@Component
class LeaderboardResponseMapper(
    private val scoreMetricsResponseMapper: ScoreMetricsResponseMapper,
) {
    fun toResponse(entry: LeaderboardEntry): LeaderboardEntryResponse =
        LeaderboardEntryResponse(
            applicationId = entry.applicationId.value,
            candidate =
                PersonSummaryResponse(
                    id = entry.candidateId.value,
                    name = entry.candidateName,
                    role = PersonRole.CANDIDATE,
                ),
            resumeId = entry.resumeId.value,
            formId = entry.formId.value,
            status = entry.status,
            score = entry.score,
            scoreMetrics = entry.scoreMetrics?.let(scoreMetricsResponseMapper::toResponse),
        )
}
