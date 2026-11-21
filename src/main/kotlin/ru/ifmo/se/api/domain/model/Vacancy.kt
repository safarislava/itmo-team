package ru.ifmo.se.api.domain.model

import java.time.Instant

data class Vacancy(
    val id: VacancyId,
    val title: String,
    val description: String,
    val teamId: TeamId,
    val city: String?,
    val status: VacancyStatus,
    val salary: Int?,
    val skillIds: List<SkillId>,
    val createdAt: Instant,
    val closedAt: Instant?,
)
