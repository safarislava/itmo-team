package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.ActivityMetrics
import ru.ifmo.se.api.domain.model.HiringApplicationsMetrics
import ru.ifmo.se.api.domain.model.HiringStatistics
import ru.ifmo.se.api.domain.model.PersonStatistics
import ru.ifmo.se.api.domain.model.ProductivityPoint
import ru.ifmo.se.api.presentation.dto.response.ActivityMetricsResponse
import ru.ifmo.se.api.presentation.dto.response.HiringApplicationsResponse
import ru.ifmo.se.api.presentation.dto.response.HiringStatisticsResponse
import ru.ifmo.se.api.presentation.dto.response.PersonStatisticsResponse
import ru.ifmo.se.api.presentation.dto.response.PersonSummaryResponse
import ru.ifmo.se.api.presentation.dto.response.ProductivityPointResponse

@Component
class AnalyticsResponseMapper {
    fun toResponse(domain: ActivityMetrics): ActivityMetricsResponse =
        ActivityMetricsResponse(
            countOpenedPr = domain.countOpenedPr,
            countClosedPr = domain.countClosedPr,
            countReviewedPr = domain.countReviewedPr,
            countCommits = domain.countCommits,
        )

    fun toResponse(domain: ProductivityPoint): ProductivityPointResponse =
        ProductivityPointResponse(
            periodStart = domain.periodStart,
            countOpenedPr = domain.metrics.countOpenedPr,
            countClosedPr = domain.metrics.countClosedPr,
            countReviewedPr = domain.metrics.countReviewedPr,
            countCommits = domain.metrics.countCommits,
        )

    fun toResponse(domain: PersonStatistics): PersonStatisticsResponse =
        PersonStatisticsResponse(
            person =
                PersonSummaryResponse(
                    id = domain.person.id.value,
                    name = domain.person.name,
                    role = domain.person.role,
                ),
            totals = toResponse(domain.totals),
            points = domain.points.map(::toResponse),
            granularity = domain.granularity,
        )

    fun toResponse(domain: HiringApplicationsMetrics): HiringApplicationsResponse =
        HiringApplicationsResponse(
            total = domain.total,
            byStatus = domain.byStatus,
        )

    fun toResponse(domain: HiringStatistics): HiringStatisticsResponse =
        HiringStatisticsResponse(
            from = domain.from,
            to = domain.to,
            openVacancies = domain.openVacancies,
            closedVacancies = domain.closedVacancies,
            applications = toResponse(domain.applications),
        )
}
