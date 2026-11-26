package ru.ifmo.se.api.infrastructure.redis.repository.adapter

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.RefreshToken
import ru.ifmo.se.api.domain.repository.RefreshTokenRepository
import ru.ifmo.se.api.infrastructure.redis.entity.RefreshTokenEntity
import ru.ifmo.se.api.infrastructure.redis.mapper.RefreshTokenEntityMapper
import ru.ifmo.se.api.infrastructure.redis.resolver.PersonRefreshTokensKeyResolver
import ru.ifmo.se.api.infrastructure.redis.resolver.RefreshTokenKeyResolver
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

@Repository
class RefreshTokenRepositoryImpl(
    private val entityRedisTemplate: RedisTemplate<String, RefreshTokenEntity>,
    private val stringRedisTemplate: StringRedisTemplate,
    private val mapper: RefreshTokenEntityMapper,
    private val refreshTokenKeyResolver: RefreshTokenKeyResolver,
    private val personRefreshTokensKeyResolver: PersonRefreshTokensKeyResolver,
) : RefreshTokenRepository {
    override fun findByTokenHash(tokenHash: String): RefreshToken? =
        entityRedisTemplate
            .opsForValue()
            .get(refreshTokenKeyResolver.resolveKey(tokenHash))
            ?.let(mapper::toDomain)

    override fun consumeByTokenHash(tokenHash: String): RefreshToken? {
        val entity =
            entityRedisTemplate
                .opsForValue()
                .getAndDelete(refreshTokenKeyResolver.resolveKey(tokenHash))
                ?: return null
        stringRedisTemplate.opsForSet().remove(personRefreshTokensKeyResolver.resolveKey(entity.personId), entity.tokenHash)
        return mapper.toDomain(entity)
    }

    override fun save(token: RefreshToken): RefreshToken {
        val entity = mapper.toEntity(token)
        val ttl = Duration.between(Instant.now(), token.expiresAt)

        if (token.revoked || ttl.isZero || ttl.isNegative) {
            deleteEntity(entity)
            return token
        }

        entityRedisTemplate.opsForValue().set(refreshTokenKeyResolver.resolveKey(entity.tokenHash), entity, ttl)

        val personKey = personRefreshTokensKeyResolver.resolveKey(entity.personId)
        stringRedisTemplate.opsForSet().add(personKey, entity.tokenHash)
        val currentTtlMillis = stringRedisTemplate.getExpire(personKey, TimeUnit.MILLISECONDS)
        if (currentTtlMillis < ttl.toMillis()) {
            stringRedisTemplate.expire(personKey, ttl)
        }

        return token
    }

    override fun revokeAllByPersonId(personId: PersonId) {
        val personKey = personRefreshTokensKeyResolver.resolveKey(personId)
        val hashes = stringRedisTemplate.opsForSet().members(personKey).orEmpty()
        stringRedisTemplate.delete(hashes.map(refreshTokenKeyResolver::resolveKey) + personKey)
    }

    private fun deleteEntity(entity: RefreshTokenEntity) {
        stringRedisTemplate.delete(refreshTokenKeyResolver.resolveKey(entity.tokenHash))
        stringRedisTemplate.opsForSet().remove(personRefreshTokensKeyResolver.resolveKey(entity.personId), entity.tokenHash)
    }
}
