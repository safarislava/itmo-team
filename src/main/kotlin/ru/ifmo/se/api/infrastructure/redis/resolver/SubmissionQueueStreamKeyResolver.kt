package ru.ifmo.se.api.infrastructure.redis.resolver

import org.springframework.stereotype.Component
import ru.ifmo.se.api.infrastructure.redis.KeyResolver
import java.time.Duration

@Component
class SubmissionQueueStreamKeyResolver : KeyResolver {
    fun resolveKey(): String = STREAM_KEY

    override fun resolveKey(key: Any): String = STREAM_KEY

    override fun getTtl(): Duration = Duration.ZERO

    private companion object {
        private const val STREAM_KEY = "submission:queue:stream"
    }
}
