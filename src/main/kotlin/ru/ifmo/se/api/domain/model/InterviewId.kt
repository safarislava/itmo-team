package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class InterviewId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): InterviewId = InterviewId(uuidV7())

        fun from(raw: String): InterviewId = InterviewId(UUID.fromString(raw))

        fun fromOrNull(raw: String): InterviewId? = runCatching { InterviewId(UUID.fromString(raw)) }.getOrNull()
    }
}
