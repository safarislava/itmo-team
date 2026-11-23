package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class SubmissionId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): SubmissionId = SubmissionId(uuidV7())

        fun from(raw: String): SubmissionId = SubmissionId(UUID.fromString(raw))

        fun fromOrNull(raw: String): SubmissionId? = runCatching { SubmissionId(UUID.fromString(raw)) }.getOrNull()
    }
}
