package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.VacancyStatus
import java.util.UUID

data class VacancySummaryResponse(
    val id: UUID,
    val title: String,
    val status: VacancyStatus,
)
