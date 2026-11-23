package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class LanguageId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): LanguageId = LanguageId(uuidV7())

        fun from(raw: String): LanguageId = LanguageId(UUID.fromString(raw))

        fun fromOrNull(raw: String): LanguageId? = runCatching { LanguageId(UUID.fromString(raw)) }.getOrNull()
    }
}
