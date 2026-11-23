package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.PersonRole
import java.util.UUID

data class PersonResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val role: PersonRole,
    val company: CompanySummaryResponse? = null,
    val team: TeamSummaryResponse? = null,
    val githubUsername: String? = null,
)
