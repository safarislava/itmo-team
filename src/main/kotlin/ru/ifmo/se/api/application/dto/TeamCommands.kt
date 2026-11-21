package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PersonId

data class CreateTeamCommand(
    val name: String,
    val teamleadId: PersonId,
    val companyId: CompanyId? = null,
)

data class UpdateTeamCommand(
    val name: String,
    val teamleadId: PersonId? = null,
)
