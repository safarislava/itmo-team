package ru.ifmo.se.api.infrastructure.redis

import java.time.Duration

interface KeyResolver {
    fun resolveKey(key: Any): String

    fun getTtl(): Duration
}
