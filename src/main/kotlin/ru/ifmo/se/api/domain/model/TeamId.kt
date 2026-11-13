package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class TeamId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): TeamId = TeamId(UUID.randomUUID())

        fun from(raw: String): TeamId = TeamId(UUID.fromString(raw))
    }
}
