package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class ContestId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): ContestId = ContestId(uuidV7())

        fun from(raw: String): ContestId = ContestId(UUID.fromString(raw))

        fun fromOrNull(raw: String): ContestId? = runCatching { ContestId(UUID.fromString(raw)) }.getOrNull()
    }
}
