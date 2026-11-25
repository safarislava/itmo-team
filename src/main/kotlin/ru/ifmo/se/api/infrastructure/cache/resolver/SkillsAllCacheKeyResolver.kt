package ru.ifmo.se.api.infrastructure.cache.resolver

import org.springframework.stereotype.Component
import ru.ifmo.se.api.infrastructure.cache.CacheKeyResolver
import java.time.Duration

@Component("skills_all")
class SkillsAllCacheKeyResolver : CacheKeyResolver {
    override fun resolveKey(key: Any): String = "dict:skills:all"

    override fun getTtl(): Duration = Duration.ofHours(24)
}
