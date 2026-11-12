package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "contest")
class ContestEntity(
    @Id
    @Column(name = "contest_id")
    override var id: UUID,

    var scheduledTime: Instant,
    var duration: Duration
) : BasicEntity()
