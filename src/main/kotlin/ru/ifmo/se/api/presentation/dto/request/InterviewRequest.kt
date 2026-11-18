package ru.ifmo.se.api.presentation.dto.request

import java.time.Instant
import java.util.UUID

data class InterviewRequest(
    val applicationId: UUID,
    val scheduledStart: Instant,
    val scheduledEnd: Instant,
)
