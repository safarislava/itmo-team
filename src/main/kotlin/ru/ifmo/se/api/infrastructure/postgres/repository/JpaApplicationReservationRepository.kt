package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationReservationEntity
import java.time.Instant
import java.util.UUID

interface JpaApplicationReservationRepository : JpaRepository<ApplicationReservationEntity, UUID> {
    fun findByApplicationId(applicationId: UUID): ApplicationReservationEntity?

    fun deleteByApplicationId(applicationId: UUID)

    fun deleteByExpiresAtBefore(now: Instant): Int
}
