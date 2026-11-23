package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class TaskId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): TaskId = TaskId(uuidV7())

        fun from(raw: String): TaskId = TaskId(UUID.fromString(raw))

        fun fromOrNull(raw: String): TaskId? = runCatching { TaskId(UUID.fromString(raw)) }.getOrNull()
    }
}
