package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class TestId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): TestId = TestId(uuidV7())

        fun from(raw: String): TestId = TestId(UUID.fromString(raw))

        fun fromOrNull(raw: String): TestId? = runCatching { TestId(UUID.fromString(raw)) }.getOrNull()
    }
}
