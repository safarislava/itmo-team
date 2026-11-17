package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.TeamId

data class CreatePersonCommand(
    val name: String,
    val email: String,
    val role: PersonRole,
    val companyId: CompanyId?,
    val teamId: TeamId?,
)

data class UpdatePersonCommand(
    val role: PersonRole,
    val companyId: CompanyId? = null,
    val teamId: TeamId?,
)

data class UpdateProfileCommand(
    val name: String,
    val email: String,
    val githubId: Long?,
)

data class ChangePasswordCommand(
    val currentPassword: String,
    val newPassword: String,
)
