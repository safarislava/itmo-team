package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class LanguageId(
    val value: UUID,
) {
    companion object {
        fun generate(): LanguageId = LanguageId(UUID.randomUUID())
    }
}
