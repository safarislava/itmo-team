package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LanguageRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 50)
    val name: String,
    @field:NotBlank
    @field:Size(min = 1, max = 20)
    val version: String,
)
