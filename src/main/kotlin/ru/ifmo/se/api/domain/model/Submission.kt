package ru.ifmo.se.api.domain.model

import java.time.Instant

data class Submission(
    val id: SubmissionId,
    val senderId: PersonId,
    val contestId: ContestId,
    val taskId: TaskId,
    val languageId: LanguageId,
    val snapshotS3key: String,
    val status: SubmissionStatus,
    val attempt: Int,
    val aiScore: Int?,
    val submittedAt: Instant,
    val response: SubmissionResponse? = null,
)
