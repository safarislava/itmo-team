package ru.ifmo.se.api.application.port.out

import ru.ifmo.se.api.domain.model.SubmissionId

interface SubmissionQueue {
    fun enqueue(submissionId: SubmissionId)

    fun enqueueMissing(submissionIds: List<SubmissionId>): Int
}
