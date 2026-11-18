package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class ResumeId(
    val value: UUID,
) {
    companion object {
        fun generate(): ResumeId = ResumeId(UUID.randomUUID())
    }
}
