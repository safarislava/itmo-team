package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import ru.ifmo.se.api.application.annotation.WriteDataAccess
import ru.ifmo.se.api.application.port.out.SubmissionQueue
import ru.ifmo.se.api.domain.repository.SubmissionRepository

@Service
class SubmissionQueueReconcileService(
    private val submissionRepository: SubmissionRepository,
    private val submissionQueue: SubmissionQueue,
) {
    @WriteDataAccess
    fun reconcile(batchSize: Int): Int {
        val queuedIds = submissionRepository.findQueued(batchSize)
        return submissionQueue.enqueueMissing(queuedIds)
    }
}
