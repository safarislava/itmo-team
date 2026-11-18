package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class FormId(
    val value: UUID,
) {
    companion object {
        fun generate(): FormId = FormId(UUID.randomUUID())
    }
}
