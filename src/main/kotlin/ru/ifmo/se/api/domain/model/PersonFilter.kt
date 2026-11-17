package ru.ifmo.se.api.domain.model

data class PersonFilter(
    val search: String? = null,
    val role: PersonRole? = null,
    val companyId: CompanyId? = null,
    val teamId: TeamId? = null,
)
