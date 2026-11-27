package ru.ifmo.se.api.infrastructure.redis

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.submission-queue")
data class SubmissionQueueProperties(
    val consumerGroup: String,
    val maxLen: Long,
    val reconcile: ReconcileProperties,
) {
    data class ReconcileProperties(
        val enabled: Boolean,
        val delayMs: Long,
        val batchSize: Int,
    )
}
