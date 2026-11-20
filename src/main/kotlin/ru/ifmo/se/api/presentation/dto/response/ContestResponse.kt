package ru.ifmo.se.api.presentation.dto.response

import java.time.Duration
import java.time.Instant
import java.util.UUID

data class ContestResponse(
    val id: UUID,
    val name: String,
    val scheduledTime: Instant,
    val duration: Duration,
    val endTime: Instant,
)
