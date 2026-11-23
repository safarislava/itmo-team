package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class ApplicationResponseId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): ApplicationResponseId = ApplicationResponseId(UUID.randomUUID())

        fun from(raw: String): ApplicationResponseId = ApplicationResponseId(UUID.fromString(raw))

        fun fromOrNull(raw: String): ApplicationResponseId? = runCatching { ApplicationResponseId(UUID.fromString(raw)) }.getOrNull()
    }
}
