package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.util.UUID

data class VacancyRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 255)
    val title: String,
    @field:NotBlank
    val description: String,
    @field:Size(max = 255)
    val city: String? = null,
    @field:PositiveOrZero
    val salary: Int? = null,
    val skillIds: List<UUID> = emptyList(),
)
