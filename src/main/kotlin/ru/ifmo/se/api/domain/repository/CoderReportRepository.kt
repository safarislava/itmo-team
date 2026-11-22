package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.CoderReport
import ru.ifmo.se.api.domain.model.CoderReportId
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.TeamId
import java.time.Instant

interface CoderReportRepository {
    fun findById(id: CoderReportId): CoderReport?

    fun save(report: CoderReport): CoderReport

    fun findByCoderIdAndReportDateBetween(
        coderId: PersonId,
        from: Instant,
        to: Instant,
    ): List<CoderReport>

    fun findByTeamIdAndReportDateBetween(
        teamId: TeamId,
        from: Instant,
        to: Instant,
    ): List<CoderReport>

    fun findByCompanyIdAndReportDateBetween(
        companyId: CompanyId,
        from: Instant,
        to: Instant,
    ): List<CoderReport>

    fun findAllByReportDateBetween(
        from: Instant,
        to: Instant,
    ): List<CoderReport>
}
