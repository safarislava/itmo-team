package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class PersonId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): PersonId = PersonId(uuidV7())

        fun from(raw: String): PersonId = PersonId(UUID.fromString(raw))

        fun fromOrNull(raw: String): PersonId? = runCatching { PersonId(UUID.fromString(raw)) }.getOrNull()
    }
}
