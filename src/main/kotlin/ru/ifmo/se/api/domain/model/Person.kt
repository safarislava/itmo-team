package ru.ifmo.se.api.domain.model

data class Person(
    val id: PersonId,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: PersonRole,
    val companyId: CompanyId?,
    val teamId: TeamId?,
    val githubUsername: String?,
)
