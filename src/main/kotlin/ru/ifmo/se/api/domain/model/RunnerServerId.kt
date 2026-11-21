package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class RunnerServerId(
    val value: UUID,
) {
    companion object {
        fun generate(): RunnerServerId = RunnerServerId(UUID.randomUUID())
    }
}
