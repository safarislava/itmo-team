package ru.ifmo.se.api.infrastructure.redis.entity

import java.time.Instant
import java.util.UUID

class RefreshTokenEntity(
    val personId: UUID,
    val tokenHash: String,
    val expiresAt: Instant,
    val createdAt: Instant,
)
