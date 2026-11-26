package ru.ifmo.se.api.infrastructure.redis.resolver

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.port.out.RefreshTokenPolicy
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.infrastructure.redis.KeyResolver
import java.time.Duration

@Component
class PersonRefreshTokensKeyResolver(
    private val refreshTokenPolicy: RefreshTokenPolicy,
) : KeyResolver {
    override fun resolveKey(key: Any): String {
        val id = (key as? PersonId) ?: PersonId.from(key.toString())
        return "auth:refresh-token:person:$id"
    }

    override fun getTtl(): Duration = Duration.ofSeconds(refreshTokenPolicy.expirationSeconds)
}
