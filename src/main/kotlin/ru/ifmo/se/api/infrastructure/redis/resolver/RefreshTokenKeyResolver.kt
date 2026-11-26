package ru.ifmo.se.api.infrastructure.redis.resolver

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.port.out.RefreshTokenPolicy
import ru.ifmo.se.api.infrastructure.redis.KeyResolver
import java.time.Duration

@Component
class RefreshTokenKeyResolver(
    private val refreshTokenPolicy: RefreshTokenPolicy,
) : KeyResolver {
    override fun resolveKey(key: Any): String {
        val tokenHash = key.toString().trim()
        check(tokenHash.isNotBlank()) { "Token hash cannot be blank" }
        return "auth:refresh-token:hash:$tokenHash"
    }

    override fun getTtl(): Duration = Duration.ofSeconds(refreshTokenPolicy.expirationSeconds)
}
