package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.SubmissionId
import ru.ifmo.se.api.domain.model.SubmissionResponse

interface SubmissionResponseRepository {
    fun findById(submissionId: SubmissionId): SubmissionResponse?

    fun save(response: SubmissionResponse): SubmissionResponse

    fun delete(submissionId: SubmissionId)
}
