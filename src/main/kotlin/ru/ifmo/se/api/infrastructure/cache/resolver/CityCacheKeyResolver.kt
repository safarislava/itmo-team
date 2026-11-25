package ru.ifmo.se.api.infrastructure.cache.resolver

import org.springframework.stereotype.Component
import ru.ifmo.se.api.infrastructure.cache.CacheKeyResolver
import java.time.Duration

@Component("cities")
class CityCacheKeyResolver : CacheKeyResolver {
    override fun resolveKey(key: Any): String {
        val city = key.toString().trim()
        check(city.isNotBlank()) { "City name cannot be blank" }
        return "dict:cities:$city"
    }

    override fun getTtl(): Duration = Duration.ofDays(30)
}
