package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.CoderReport
import ru.ifmo.se.api.domain.model.CoderReportId
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.repository.CoderReportRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.CoderReportEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaCoderReportRepository
import java.time.LocalDate
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

    @Cacheable(
        value = ["coder_reports"],
        key = "#coderId + ':' + #from + ':' + #to",
    )
    override fun findByCoderIdAndReportDateBetween(
        coderId: PersonId,
        from: LocalDate,
        to: LocalDate,
    ): List<CoderReport> =
        jpaCoderReportRepository
            .findByCoderIdAndReportDateGreaterThanEqualAndReportDateLessThanEqualOrderByReportDateAsc(
                coderId.value,
                from,
                to,
            ).map(coderReportEntityMapper::toDomain)
}
