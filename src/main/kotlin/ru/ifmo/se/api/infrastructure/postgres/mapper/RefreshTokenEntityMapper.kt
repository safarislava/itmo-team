package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.RefreshToken
import ru.ifmo.se.api.domain.model.RefreshTokenId
import ru.ifmo.se.api.infrastructure.postgres.entity.RefreshTokenEntity

@Component
class RefreshTokenEntityMapper {
    fun toDomain(entity: RefreshTokenEntity): RefreshToken =
        RefreshToken(
            id = RefreshTokenId(entity.id),
            personId = PersonId(entity.personId),
            tokenHash = entity.tokenHash,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt,
            revoked = entity.revoked,
        )

    fun toEntity(domain: RefreshToken): RefreshTokenEntity =
        RefreshTokenEntity(
            id = domain.id.value,
            personId = domain.personId.value,
            tokenHash = domain.tokenHash,
            expiresAt = domain.expiresAt,
            createdAt = domain.createdAt,
            revoked = domain.revoked,
        )
}
