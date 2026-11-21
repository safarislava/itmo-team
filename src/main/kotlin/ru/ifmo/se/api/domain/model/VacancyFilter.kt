package ru.ifmo.se.api.domain.model

data class VacancyFilter(
    val search: String? = null,
    val skillIds: Set<SkillId>? = null,
    val city: String? = null,
    val minSalary: Int? = null,
    val status: VacancyStatus? = null,
    val companyId: CompanyId? = null,
    val teamId: TeamId? = null,
)
