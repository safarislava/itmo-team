package ru.ifmo.se.api.infrastructure.redis.queue

import org.springframework.data.domain.Range
import org.springframework.data.redis.connection.Limit
import org.springframework.data.redis.connection.RedisStreamCommands.TrimOptions
import org.springframework.data.redis.connection.RedisStreamCommands.XAddOptions
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.StreamInfo.XInfoGroup
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.port.out.SubmissionQueue
import ru.ifmo.se.api.domain.model.SubmissionId
import ru.ifmo.se.api.infrastructure.redis.SubmissionQueueProperties
import ru.ifmo.se.api.infrastructure.redis.resolver.SubmissionQueueStreamKeyResolver

@Component
class RedisSubmissionQueue(
    stringRedisTemplate: StringRedisTemplate,
    private val properties: SubmissionQueueProperties,
    streamKeyResolver: SubmissionQueueStreamKeyResolver,
) : SubmissionQueue {
    private val streamKey = streamKeyResolver.resolveKey()
    private val streamOps = stringRedisTemplate.opsForStream<String, String>()

    override fun enqueue(submissionId: SubmissionId) {
        streamOps.add(
            streamKey,
            mapOf(FIELD_SUBMISSION_ID to submissionId.toString()),
            XAddOptions.trim(TrimOptions.maxLen(properties.maxLen).approximate()),
        )
    }

    override fun enqueueMissing(submissionIds: List<SubmissionId>): Int {
        if (submissionIds.isEmpty()) return 0

        val activeIds = loadActiveSubmissionIds()
        val remaining = (properties.maxLen - activeIds.size).coerceAtLeast(0L).toInt()
        if (remaining <= 0) return 0

        return submissionIds
            .asSequence()
            .filterNot { it in activeIds }
            .take(remaining)
            .count { runCatching { enqueue(it) }.isSuccess }
    }

    private fun loadActiveSubmissionIds(): Set<SubmissionId> {
        val group = findGroup()
        val unread =
            streamOps
                .range(
                    streamKey,
                    Range.rightUnbounded(Range.Bound.exclusive(group.lastDeliveredId())),
                    Limit.limit().count(properties.maxLen.toInt()),
                ).orEmpty()
        val pending = if (group.pendingCount() > 0L) loadPendingRecords() else emptyList()

        return (unread + pending)
            .mapNotNullTo(HashSet()) { it.value[FIELD_SUBMISSION_ID]?.let(SubmissionId::fromOrNull) }
    }

    private fun loadPendingRecords(): List<MapRecord<String, String, String>> {
        val pending = streamOps.pending(streamKey, properties.consumerGroup, Range.unbounded<String>(), properties.maxLen)
        if (pending.isEmpty) return emptyList()

        val pendingIds = pending.mapTo(HashSet()) { it.id }
        val range = Range.closed(pending.get(0).idAsString, pending.get(pending.size() - 1).idAsString)
        return streamOps
            .range(streamKey, range, Limit.unlimited())
            .orEmpty()
            .filter { it.id in pendingIds }
    }

    private fun findGroup(): XInfoGroup =
        streamOps
            .groups(streamKey)
            .firstOrNull { it.groupName() == properties.consumerGroup }
            ?: error("Consumer group '${properties.consumerGroup}' not found for stream '$streamKey'")

    private companion object {
        private const val FIELD_SUBMISSION_ID = "submissionId"
    }
}
