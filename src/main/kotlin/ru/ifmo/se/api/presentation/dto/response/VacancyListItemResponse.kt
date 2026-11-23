package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.VacancyStatus
import java.time.Instant
import java.util.UUID

data class VacancyListItemResponse(
    val id: UUID,
    val title: String,
    val city: String?,
    val salary: Int?,
    val status: VacancyStatus,
    val createdAt: Instant,
    val companyId: UUID,
    val companyName: String,
)
