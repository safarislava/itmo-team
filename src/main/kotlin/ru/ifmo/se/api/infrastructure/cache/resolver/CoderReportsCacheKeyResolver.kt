package ru.ifmo.se.api.infrastructure.cache.resolver

import org.springframework.stereotype.Component
import ru.ifmo.se.api.infrastructure.cache.CacheKeyResolver
import java.time.Duration

@Component("coder_reports")
class CoderReportsCacheKeyResolver : CacheKeyResolver {
    override fun resolveKey(key: Any): String {
        val raw = key.toString().trim()
        check(raw.isNotBlank()) { "Coder reports cache key cannot be blank" }
        return "analytics:coder-reports:$raw"
    }

    override fun getTtl(): Duration = Duration.ofDays(7)
}
