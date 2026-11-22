package ru.ifmo.se.api.domain.model

data class LeaderboardFilter(
    val vacancyId: VacancyId,
    val motivationWeight: Double = 1.0,
    val softSkillsWeight: Double = 1.0,
    val petProjectsWeight: Double = 1.0,
    val jobExpWeight: Double = 1.0,
    val hardSkillsWeight: Double = 1.0,
    val workFormatWeight: Double = 1.0,
    val minScore: Double? = null,
    val status: ApplicationStatus? = null,
    val skillIds: Set<SkillId>? = null,
)
