package ru.ifmo.se.api.infrastructure.cache.resolver

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.LanguageId
import ru.ifmo.se.api.infrastructure.cache.CacheKeyResolver
import java.time.Duration

@Component("languages")
class LanguageCacheKeyResolver : CacheKeyResolver {
    override fun resolveKey(key: Any): String {
        val id = (key as? LanguageId) ?: LanguageId.from(key.toString())
        return "dict:languages:$id"
    }

    override fun getTtl(): Duration = Duration.ofDays(7)
}
