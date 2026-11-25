package ru.ifmo.se.api.infrastructure.cache

import java.time.Duration

interface CacheKeyResolver {
    fun resolveKey(key: Any): String

    fun getTtl(): Duration
}
