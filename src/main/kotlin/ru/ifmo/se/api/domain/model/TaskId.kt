package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class TaskId(
    val value: UUID,
) {
    companion object {
        fun generate(): TaskId = TaskId(UUID.randomUUID())
    }
}
