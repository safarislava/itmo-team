package ru.ifmo.se.api.infrastructure.postgres.projection

import ru.ifmo.se.api.domain.model.VacancyStatus
import java.time.Instant
import java.util.UUID

data class VacancySummaryProjection(
    val id: UUID,
    val title: String,
    val city: String?,
    val salary: Int?,
    val status: VacancyStatus,
    val createdAt: Instant,
    val companyId: UUID,
    val companyName: String,
)
