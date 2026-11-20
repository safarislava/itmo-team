package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.UUID

@Embeddable
data class ContestTaskId(
    @Column(name = "contest_id")
    var contestId: UUID,
    @Column(name = "task_id")
    var taskId: UUID,
) : Serializable
