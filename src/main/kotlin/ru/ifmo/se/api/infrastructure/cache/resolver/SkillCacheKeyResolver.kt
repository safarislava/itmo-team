package ru.ifmo.se.api.infrastructure.cache.resolver

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.SkillId
import ru.ifmo.se.api.infrastructure.cache.CacheKeyResolver
import java.time.Duration

@Component("skills")
class SkillCacheKeyResolver : CacheKeyResolver {
    override fun resolveKey(key: Any): String {
        val id = (key as? SkillId) ?: SkillId.from(key.toString())
        return "dict:skills:$id"
    }

    override fun getTtl(): Duration = Duration.ofDays(7)
}
