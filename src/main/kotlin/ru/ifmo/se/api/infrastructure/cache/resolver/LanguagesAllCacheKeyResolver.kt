package ru.ifmo.se.api.infrastructure.cache.resolver

import org.springframework.stereotype.Component
import ru.ifmo.se.api.infrastructure.cache.CacheKeyResolver
import java.time.Duration

@Component("languages_all")
class LanguagesAllCacheKeyResolver : CacheKeyResolver {
    override fun resolveKey(key: Any): String = "dict:languages:all"

    override fun getTtl(): Duration = Duration.ofHours(24)
}
