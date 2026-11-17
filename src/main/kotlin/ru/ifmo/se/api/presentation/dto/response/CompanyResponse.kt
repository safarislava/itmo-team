package ru.ifmo.se.api.presentation.dto.response

import java.time.LocalDate
import java.util.UUID

data class CompanyResponse(
    val id: UUID,
    val name: String,
    val creationDate: LocalDate,
)
