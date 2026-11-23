package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class FormId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): FormId = FormId(UUID.randomUUID())

        fun from(raw: String): FormId = FormId(UUID.fromString(raw))

        fun fromOrNull(raw: String): FormId? = runCatching { FormId(UUID.fromString(raw)) }.getOrNull()
    }
}
