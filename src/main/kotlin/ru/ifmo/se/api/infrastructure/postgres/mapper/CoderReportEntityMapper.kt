package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.ActivityMetrics
import ru.ifmo.se.api.domain.model.CoderReport
import ru.ifmo.se.api.domain.model.CoderReportId
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.infrastructure.postgres.entity.CoderReportEntity
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaPersonRepository

@Component
class CoderReportEntityMapper(
    private val jpaPersonRepository: JpaPersonRepository,
) {
    fun toDomain(entity: CoderReportEntity): CoderReport =
        CoderReport(
            id = CoderReportId(entity.id),
            coderId = entity.coder?.id?.let { PersonId(it) },
            metrics =
                ActivityMetrics(
                    countOpenedPr = entity.countOpenedPr,
                    countClosedPr = entity.countClosedPr,
                    countReviewedPr = entity.countReviewedPr,
                    countCommits = entity.countCommits,
                ),
            reportDate = entity.reportDate,
        )

    fun toEntity(domain: CoderReport): CoderReportEntity =
        CoderReportEntity(
            id = domain.id.value,
            coder = domain.coderId?.let { jpaPersonRepository.getReferenceById(it.value) },
            countOpenedPr = domain.metrics.countOpenedPr,
            countClosedPr = domain.metrics.countClosedPr,
            countReviewedPr = domain.metrics.countReviewedPr,
            countCommits = domain.metrics.countCommits,
            reportDate = domain.reportDate,
        )
}
