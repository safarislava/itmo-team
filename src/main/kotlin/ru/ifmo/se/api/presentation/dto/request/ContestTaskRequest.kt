package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.Min

data class ContestTaskRequest(
    @field:Min(1)
    val maxAttempts: Int,
)
