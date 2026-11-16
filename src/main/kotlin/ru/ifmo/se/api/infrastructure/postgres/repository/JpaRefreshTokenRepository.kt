package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import ru.ifmo.se.api.infrastructure.postgres.entity.RefreshTokenEntity
import java.util.UUID

interface JpaRefreshTokenRepository : JpaRepository<RefreshTokenEntity, UUID> {
    fun findByTokenHash(tokenHash: String): RefreshTokenEntity?

    fun findAllByPersonId(personId: UUID): List<RefreshTokenEntity>

    @Modifying
    @Query("UPDATE RefreshTokenEntity t SET t.revoked = true WHERE t.personId = :personId AND t.revoked = false")
    fun revokeAllByPersonId(personId: UUID)
}
