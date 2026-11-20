package ru.ifmo.se.api.domain.model

import java.time.Duration
import java.time.Instant

data class Contest(
    val id: ContestId,
    val scheduledTime: Instant,
    val duration: Duration,
) {
    val endTime: Instant
        get() = scheduledTime.plus(duration)
}
