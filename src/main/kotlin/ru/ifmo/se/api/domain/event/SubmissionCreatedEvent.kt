package ru.ifmo.se.api.domain.event

import ru.ifmo.se.api.domain.model.SubmissionId

data class SubmissionCreatedEvent(
    val submissionId: SubmissionId,
    val sourceCode: String,
    val languageName: String,
)
