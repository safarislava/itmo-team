package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class InterviewFeedbackId(
    val value: UUID,
) {
    companion object {
        fun generate(): InterviewFeedbackId = InterviewFeedbackId(UUID.randomUUID())
    }
}
