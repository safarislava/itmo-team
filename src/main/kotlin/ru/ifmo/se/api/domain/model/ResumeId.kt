package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class ResumeId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): ResumeId = ResumeId(uuidV7())

        fun from(raw: String): ResumeId = ResumeId(UUID.fromString(raw))

        fun fromOrNull(raw: String): ResumeId? = runCatching { ResumeId(UUID.fromString(raw)) }.getOrNull()
    }
}
