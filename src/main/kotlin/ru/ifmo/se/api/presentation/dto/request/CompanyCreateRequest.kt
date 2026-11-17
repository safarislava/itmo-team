package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CompanyCreateRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 255)
    val name: String,
)
