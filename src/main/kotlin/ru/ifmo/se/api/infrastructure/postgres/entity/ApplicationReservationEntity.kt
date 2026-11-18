package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "application_reservation")
class ApplicationReservationEntity(
    @Id
    @Column(name = "application_id")
    var applicationId: UUID,
    @Column(name = "hr_id")
    var hrId: UUID,
    @Column(name = "expires_at")
    var expiresAt: Instant,
    @Column(name = "created_at")
    var createdAt: Instant = Instant.now(),
)
