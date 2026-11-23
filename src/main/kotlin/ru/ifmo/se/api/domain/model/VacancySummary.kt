package ru.ifmo.se.api.domain.model

import java.time.Instant

data class VacancySummary(
    val id: VacancyId,
    val title: String,
    val city: String?,
    val salary: Int?,
    val status: VacancyStatus,
    val createdAt: Instant,
    val companyId: CompanyId,
    val companyName: String,
)
