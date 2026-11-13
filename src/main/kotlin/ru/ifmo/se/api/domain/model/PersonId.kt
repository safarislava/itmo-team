package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class PersonId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): PersonId = PersonId(UUID.randomUUID())

        fun from(raw: String): PersonId = PersonId(UUID.fromString(raw))
    }
}
