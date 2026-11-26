package ru.ifmo.se.api.infrastructure.redis.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.RefreshToken
import ru.ifmo.se.api.infrastructure.redis.entity.RefreshTokenEntity

@Component
class RefreshTokenEntityMapper {
    fun toDomain(entity: RefreshTokenEntity): RefreshToken =
        RefreshToken(
            personId = PersonId(entity.personId),
            tokenHash = entity.tokenHash,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt,
            revoked = false,
        )

    fun toEntity(domain: RefreshToken): RefreshTokenEntity =
        RefreshTokenEntity(
            personId = domain.personId.value,
            tokenHash = domain.tokenHash,
            expiresAt = domain.expiresAt,
            createdAt = domain.createdAt,
        )
}
