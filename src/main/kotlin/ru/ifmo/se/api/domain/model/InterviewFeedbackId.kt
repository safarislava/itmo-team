package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class InterviewFeedbackId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()

    companion object {
        fun generate(): InterviewFeedbackId = InterviewFeedbackId(UUID.randomUUID())

        fun from(raw: String): InterviewFeedbackId = InterviewFeedbackId(UUID.fromString(raw))

        fun fromOrNull(raw: String): InterviewFeedbackId? = runCatching { InterviewFeedbackId(UUID.fromString(raw)) }.getOrNull()
    }
}
