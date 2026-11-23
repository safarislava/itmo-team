package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class ApplicationId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): ApplicationId = ApplicationId(uuidV7())

        fun from(raw: String): ApplicationId = ApplicationId(UUID.fromString(raw))

        fun fromOrNull(raw: String): ApplicationId? = runCatching { ApplicationId(UUID.fromString(raw)) }.getOrNull()
    }
}
