package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.RefreshToken
import ru.ifmo.se.api.domain.model.RefreshTokenId
import ru.ifmo.se.api.domain.repository.RefreshTokenRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.RefreshTokenEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaRefreshTokenRepository

@Repository
class RefreshTokenRepositoryImpl(
    private val repo: JpaRefreshTokenRepository,
    private val mapper: RefreshTokenEntityMapper,
) : RefreshTokenRepository {
    override fun findById(id: RefreshTokenId): RefreshToken? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findByTokenHash(tokenHash: String): RefreshToken? = repo.findByTokenHash(tokenHash)?.let(mapper::toDomain)

    override fun findAllByPersonId(personId: PersonId): List<RefreshToken> = repo.findAllByPersonId(personId.value).map(mapper::toDomain)

    override fun save(token: RefreshToken): RefreshToken = mapper.toDomain(repo.save(mapper.toEntity(token)))

    override fun delete(token: RefreshToken) {
        repo.deleteById(token.id.value)
    }

    override fun deleteById(id: RefreshTokenId): Boolean =
        if (repo.existsById(id.value)) {
            repo.deleteById(id.value)
            true
        } else {
            false
        }

    @Transactional
    override fun revokeAllByPersonId(personId: PersonId) {
        repo.revokeAllByPersonId(personId.value)
    }
}
