package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Duration
import java.time.Instant

data class ContestRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 255)
    val name: String,
    val scheduledTime: Instant,
    val duration: Duration,
)
