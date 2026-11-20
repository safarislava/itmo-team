package ru.ifmo.se.api.application.dto

import java.time.Duration
import java.time.Instant

data class CreateContestCommand(
    val name: String,
    val scheduledTime: Instant,
    val duration: Duration,
)

data class UpdateContestCommand(
    val name: String,
    val scheduledTime: Instant,
    val duration: Duration,
)
