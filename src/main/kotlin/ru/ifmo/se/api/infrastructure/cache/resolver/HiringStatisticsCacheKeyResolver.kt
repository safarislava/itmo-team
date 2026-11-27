package ru.ifmo.se.api.infrastructure.cache.resolver

import org.springframework.stereotype.Component
import ru.ifmo.se.api.infrastructure.cache.CacheKeyResolver
import java.time.Duration

@Component("hiring_statistics")
class HiringStatisticsCacheKeyResolver : CacheKeyResolver {
    override fun resolveKey(key: Any): String {
        val raw = key.toString().trim()
        check(raw.isNotBlank()) { "Hiring statistics cache key cannot be blank" }
        return "analytics:hiring-statistics:$raw"
    }

    override fun getTtl(): Duration = Duration.ofDays(7)
}
