package ru.ifmo.se.api.infrastructure.redis.queue

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import ru.ifmo.se.api.infrastructure.redis.SubmissionQueueProperties
import ru.ifmo.se.api.infrastructure.redis.resolver.SubmissionQueueStreamKeyResolver

@Component
class RedisSubmissionStreamInitializer(
    stringRedisTemplate: StringRedisTemplate,
    private val properties: SubmissionQueueProperties,
    streamKeyResolver: SubmissionQueueStreamKeyResolver,
) {
    private val streamKey = streamKeyResolver.resolveKey()
    private val streamOps = stringRedisTemplate.opsForStream<String, String>()

    @EventListener(ApplicationReadyEvent::class)
    fun initialize() {
        runCatching {
            streamOps.createGroup(streamKey, ReadOffset.from(INITIAL_STREAM_OFFSET), properties.consumerGroup)
        }.onFailure { ex ->
            if (generateSequence(ex) { it.cause }.none { it.message?.contains("BUSYGROUP", ignoreCase = true) == true }) {
                throw ex
            }
        }
    }

    private companion object {
        private const val INITIAL_STREAM_OFFSET = "0-0"
    }
}
