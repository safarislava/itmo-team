package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.CoderReportEntity
import java.time.LocalDate
import java.util.UUID

interface JpaCoderReportRepository : JpaRepository<CoderReportEntity, UUID> {
    fun findByCoderIdAndReportDateGreaterThanEqualAndReportDateLessThanEqualOrderByReportDateAsc(
        coderId: UUID,
        from: LocalDate,
        to: LocalDate,
    ): List<CoderReportEntity>

    fun findByCoderTeamIdAndReportDateGreaterThanEqualAndReportDateLessThanEqualOrderByReportDateAsc(
        teamId: UUID,
        from: LocalDate,
        to: LocalDate,
    ): List<CoderReportEntity>

    fun findByCoderCompanyIdAndReportDateGreaterThanEqualAndReportDateLessThanEqualOrderByReportDateAsc(
        companyId: UUID,
        from: LocalDate,
        to: LocalDate,
    ): List<CoderReportEntity>

    fun findByReportDateGreaterThanEqualAndReportDateLessThanEqualOrderByReportDateAsc(
        from: LocalDate,
        to: LocalDate,
    ): List<CoderReportEntity>
}
