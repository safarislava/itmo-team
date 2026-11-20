package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class TaskRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 255)
    val name: String,
    val description: String?,
    @field:NotNull
    val timeLimit: Int,
    @field:NotNull
    val memoryLimit: Int,
)
