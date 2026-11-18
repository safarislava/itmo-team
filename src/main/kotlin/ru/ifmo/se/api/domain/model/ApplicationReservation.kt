package ru.ifmo.se.api.domain.model

import java.time.Instant

data class ApplicationReservation(
    val applicationId: ApplicationId,
    val hrId: PersonId,
    val expiresAt: Instant,
    val createdAt: Instant = Instant.now(),
)
