package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class ApplicationResponseId(
    val value: UUID,
) {
    companion object {
        fun generate(): ApplicationResponseId = ApplicationResponseId(UUID.randomUUID())
    }
}
