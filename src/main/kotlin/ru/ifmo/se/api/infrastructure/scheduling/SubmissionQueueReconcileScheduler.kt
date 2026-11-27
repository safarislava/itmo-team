package ru.ifmo.se.api.infrastructure.scheduling

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.service.SubmissionQueueReconcileService
import ru.ifmo.se.api.infrastructure.redis.SubmissionQueueProperties

@Component
@ConditionalOnProperty(
    name = ["app.submission-queue.reconcile.enabled"],
    havingValue = "true",
)
class SubmissionQueueReconcileScheduler(
    private val reconcileService: SubmissionQueueReconcileService,
    private val properties: SubmissionQueueProperties,
) {
    @Scheduled(fixedDelayString = "\${app.submission-queue.reconcile.delay-ms}")
    fun reconcileQueuedSubmissions() {
        reconcileService.reconcile(properties.reconcile.batchSize)
    }
}
