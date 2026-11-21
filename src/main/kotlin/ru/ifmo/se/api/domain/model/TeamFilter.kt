package ru.ifmo.se.api.domain.model

data class TeamFilter(
    val companyId: CompanyId? = null,
    val name: String? = null,
)
