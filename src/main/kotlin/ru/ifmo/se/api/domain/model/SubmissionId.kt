package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class SubmissionId(
    val value: UUID,
) {
    companion object {
        fun generate(): SubmissionId = SubmissionId(UUID.randomUUID())
    }
}
