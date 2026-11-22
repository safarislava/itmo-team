package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationScoreEntity
import java.util.UUID

interface JpaApplicationScoreRepository : JpaRepository<ApplicationScoreEntity, UUID> {
    fun findByApplicationId(applicationId: UUID): ApplicationScoreEntity?
}
