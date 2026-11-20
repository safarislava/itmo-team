package ru.ifmo.se.api.infrastructure.postgres.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table

@Entity
@Table(name = "contest_task")
class ContestTaskEntity(
    @EmbeddedId
    var id: ContestTaskId,
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("contestId")
    @JoinColumn(name = "contest_id")
    var contest: ContestEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "task_id")
    var task: TaskEntity,
    var maxAttempts: Short = 3,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContestTaskEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
