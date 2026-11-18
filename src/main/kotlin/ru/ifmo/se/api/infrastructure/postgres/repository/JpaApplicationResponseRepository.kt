package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationResponseEntity
import java.time.Instant
import java.util.UUID

interface JpaApplicationResponseRepository : JpaRepository<ApplicationResponseEntity, UUID> {
    fun findAllByApplicationId(applicationId: UUID): List<ApplicationResponseEntity>

    fun countByApplicationId(applicationId: UUID): Long

    @Query(
        """
        SELECT a FROM ApplicationEntity a
        WHERE a.status = ru.ifmo.se.api.domain.model.ApplicationStatus.REVIEW
          AND a.vacancy.team.company.id = :companyId
          AND NOT EXISTS (
              SELECT 1 FROM ApplicationResponseEntity ar
              WHERE ar.application.id = a.id AND ar.hr.id = :hrId
          )
          AND (
              SELECT COUNT(ar2) FROM ApplicationResponseEntity ar2
              WHERE ar2.application.id = a.id
          ) < 2
          AND NOT EXISTS (
              SELECT 1 FROM ApplicationReservationEntity res
              WHERE res.applicationId = a.id AND res.hrId <> :hrId AND res.expiresAt > :now
          )
        ORDER BY (
            SELECT COUNT(ar3) FROM ApplicationResponseEntity ar3
            WHERE ar3.application.id = a.id
        ) DESC, a.id ASC
    """,
    )
    fun findCandidatesForScreening(
        companyId: UUID,
        hrId: UUID,
        now: Instant,
        pageable: Pageable,
    ): List<ApplicationEntity>
}
