package ru.ifmo.se.api.application.listener

import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import ru.ifmo.se.api.application.service.SubmissionAiScoreService
import ru.ifmo.se.api.domain.event.SubmissionCreatedEvent

@Component
class SubmissionAiScoreEventListener(
    private val submissionAiScoreService: SubmissionAiScoreService,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onSubmissionCreated(event: SubmissionCreatedEvent) {
        submissionAiScoreService.checkAndSaveAiScore(
            submissionId = event.submissionId,
            sourceCode = event.sourceCode,
            languageName = event.languageName,
        )
    }
}
