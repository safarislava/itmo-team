package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class RunnerServerId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): RunnerServerId = RunnerServerId(uuidV7())

        fun from(raw: String): RunnerServerId = RunnerServerId(UUID.fromString(raw))

        fun fromOrNull(raw: String): RunnerServerId? = runCatching { RunnerServerId(UUID.fromString(raw)) }.getOrNull()
    }
}
