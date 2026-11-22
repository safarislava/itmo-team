package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.CoderReportEntity
import java.time.Instant
import java.util.UUID

interface JpaCoderReportRepository : JpaRepository<CoderReportEntity, UUID> {
    fun findByCoderIdAndReportDateGreaterThanEqualAndReportDateLessThanOrderByReportDateAsc(
        coderId: UUID,
        from: Instant,
        to: Instant,
    ): List<CoderReportEntity>

    fun findByCoderTeamIdAndReportDateGreaterThanEqualAndReportDateLessThanOrderByReportDateAsc(
        teamId: UUID,
        from: Instant,
        to: Instant,
    ): List<CoderReportEntity>

    fun findByCoderCompanyIdAndReportDateGreaterThanEqualAndReportDateLessThanOrderByReportDateAsc(
        companyId: UUID,
        from: Instant,
        to: Instant,
    ): List<CoderReportEntity>

    fun findByReportDateGreaterThanEqualAndReportDateLessThanOrderByReportDateAsc(
        from: Instant,
        to: Instant,
    ): List<CoderReportEntity>
}
