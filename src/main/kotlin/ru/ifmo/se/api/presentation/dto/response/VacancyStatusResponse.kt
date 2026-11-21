package ru.ifmo.se.api.presentation.dto.response

import java.util.UUID

data class VacancyStatusResponse(
    val vacancy: VacancyResponse,
    val rejectedApplicationIds: List<UUID>,
)
