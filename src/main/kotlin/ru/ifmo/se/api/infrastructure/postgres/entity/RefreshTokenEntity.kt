package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "refresh_token")
class RefreshTokenEntity(
    @Id
    @Column(name = "token_id")
    override var id: UUID,
    var personId: UUID,
    var tokenHash: String,
    var expiresAt: Instant,
    var createdAt: Instant,
    var revoked: Boolean,
) : BasicEntity()
