package ru.ifmo.se.api.application.listener

import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import ru.ifmo.se.api.application.port.out.SubmissionQueue
import ru.ifmo.se.api.domain.event.SubmissionCreatedEvent

@Component
class SubmissionQueueEventListener(
    private val submissionQueue: SubmissionQueue,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onSubmissionCreated(event: SubmissionCreatedEvent) {
        runCatching {
            submissionQueue.enqueue(event.submissionId)
        }
    }
}
