package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class ContestId(
    val value: UUID,
) {
    companion object {
        fun generate(): ContestId = ContestId(UUID.randomUUID())
    }
}
