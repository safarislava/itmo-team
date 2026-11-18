package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationResponseEntity
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
        ORDER BY (
            SELECT COUNT(ar3) FROM ApplicationResponseEntity ar3
            WHERE ar3.application.id = a.id
        ) DESC, a.id ASC
    """,
    )
    fun findCandidatesForScreening(
        @Param("companyId") companyId: UUID,
        @Param("hrId") hrId: UUID,
        pageable: Pageable,
    ): List<ApplicationEntity>
}
