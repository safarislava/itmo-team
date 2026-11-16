package ru.ifmo.se.api.presentation.dto.response

import java.util.UUID

data class TeamSummaryResponse(
    val id: UUID,
    val name: String,
)
