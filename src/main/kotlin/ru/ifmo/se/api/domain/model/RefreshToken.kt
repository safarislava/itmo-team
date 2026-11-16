package ru.ifmo.se.api.domain.model

import java.time.Instant

class RefreshToken(
    val id: RefreshTokenId,
    val personId: PersonId,
    val tokenHash: String,
    val expiresAt: Instant,
    val createdAt: Instant,
    val revoked: Boolean,
) {
    fun isExpired(now: Instant = Instant.now()): Boolean = now.isAfter(expiresAt)

    fun isValid(now: Instant = Instant.now()): Boolean = !revoked && !isExpired(now)
}
