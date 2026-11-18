package ru.ifmo.se.api.domain.model

import java.util.UUID

@JvmInline
value class InterviewId(
    val value: UUID,
) {
    companion object {
        fun generate(): InterviewId = InterviewId(UUID.randomUUID())
    }
}
