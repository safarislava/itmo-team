package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.CoderReport
import ru.ifmo.se.api.domain.model.CoderReportId
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.repository.CoderReportRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.CoderReportEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaCoderReportRepository
import java.time.Instant
import kotlin.jvm.optionals.getOrNull

@Repository
class CoderReportRepositoryImpl(
    private val jpaCoderReportRepository: JpaCoderReportRepository,
    private val coderReportEntityMapper: CoderReportEntityMapper,
) : CoderReportRepository {
    override fun findById(id: CoderReportId): CoderReport? =
        jpaCoderReportRepository.findById(id.value).getOrNull()?.let(coderReportEntityMapper::toDomain)

    override fun save(report: CoderReport): CoderReport {
        val entity = coderReportEntityMapper.toEntity(report)
        return coderReportEntityMapper.toDomain(jpaCoderReportRepository.save(entity))
    }

    override fun findByCoderIdAndReportDateBetween(
        coderId: PersonId,
        from: Instant,
        to: Instant,
    ): List<CoderReport> =
        jpaCoderReportRepository
            .findByCoderIdAndReportDateGreaterThanEqualAndReportDateLessThanOrderByReportDateAsc(
                coderId.value,
                from,
                to,
            ).map(coderReportEntityMapper::toDomain)

    override fun findByTeamIdAndReportDateBetween(
        teamId: TeamId,
        from: Instant,
        to: Instant,
    ): List<CoderReport> =
        jpaCoderReportRepository
            .findByCoderTeamIdAndReportDateGreaterThanEqualAndReportDateLessThanOrderByReportDateAsc(
                teamId.value,
                from,
                to,
            ).map(coderReportEntityMapper::toDomain)

    override fun findByCompanyIdAndReportDateBetween(
        companyId: CompanyId,
        from: Instant,
        to: Instant,
    ): List<CoderReport> =
        jpaCoderReportRepository
            .findByCoderCompanyIdAndReportDateGreaterThanEqualAndReportDateLessThanOrderByReportDateAsc(
                companyId.value,
                from,
                to,
            ).map(coderReportEntityMapper::toDomain)

    override fun findAllByReportDateBetween(
        from: Instant,
        to: Instant,
    ): List<CoderReport> =
        jpaCoderReportRepository
            .findByReportDateGreaterThanEqualAndReportDateLessThanOrderByReportDateAsc(
                from,
                to,
            ).map(coderReportEntityMapper::toDomain)
}
