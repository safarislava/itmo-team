package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class TeamRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val name: String,
    val teamleadId: UUID? = null,
    val companyId: UUID? = null,
)
