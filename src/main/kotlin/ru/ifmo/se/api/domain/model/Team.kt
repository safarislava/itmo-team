package ru.ifmo.se.api.domain.model

data class Team(
    val id: TeamId,
    val name: String,
    val companyId: CompanyId,
    val teamleadId: PersonId,
)
