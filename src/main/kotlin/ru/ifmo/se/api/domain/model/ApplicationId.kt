package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class ApplicationId(
    val value: UUID,
) {
    companion object {
        fun generate(): ApplicationId = ApplicationId(UUID.randomUUID())
    }
}
