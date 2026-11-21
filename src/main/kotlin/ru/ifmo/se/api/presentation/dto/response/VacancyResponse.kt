package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.VacancyStatus
import java.time.Instant
import java.util.UUID

data class VacancyResponse(
    val id: UUID,
    val title: String,
    val description: String,
    val team: TeamSummaryResponse,
    val company: CompanyResponse,
    val city: String?,
    val status: VacancyStatus,
    val salary: Int?,
    val skills: List<SkillResponse>,
    val createdAt: Instant,
    val closedAt: Instant?,
)
