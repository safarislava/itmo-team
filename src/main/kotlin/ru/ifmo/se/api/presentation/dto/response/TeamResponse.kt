package ru.ifmo.se.api.presentation.dto.response

import java.util.UUID

data class TeamResponse(
    val id: UUID,
    val name: String,
    val company: CompanyResponse,
    val teamlead: PersonSummaryResponse,
)
