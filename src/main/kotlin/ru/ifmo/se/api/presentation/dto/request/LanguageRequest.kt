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
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val sourceFilename: String,
    @field:Size(max = 500)
    val compileCommand: String? = null,
    @field:NotBlank
    @field:Size(min = 1, max = 500)
    val runCommand: String,
)
