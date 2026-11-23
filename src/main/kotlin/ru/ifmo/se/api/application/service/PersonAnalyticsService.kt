package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.annotation.ReadOnlyDataAccess
import ru.ifmo.se.api.application.policy.PersonAnalyticsPolicy
import ru.ifmo.se.api.domain.exception.AnalyticsNotFoundException
import ru.ifmo.se.api.domain.model.ActivityMetrics
import ru.ifmo.se.api.domain.model.CoderReport
import ru.ifmo.se.api.domain.model.Granularity
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonStatistics
import ru.ifmo.se.api.domain.model.ProductivityPoint
import ru.ifmo.se.api.domain.repository.CoderReportRepository
import ru.ifmo.se.api.domain.repository.PersonRepository
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters

@Service
@Transactional(readOnly = true)
@ReadOnlyDataAccess
class PersonAnalyticsService(
    private val personAnalyticsPolicy: PersonAnalyticsPolicy,
    private val personRepository: PersonRepository,
    private val coderReportRepository: CoderReportRepository,
) {
    fun getPersonStatistics(
        caller: Person,
        personId: PersonId,
        from: Instant,
        to: Instant,
        granularity: Granularity,
    ): PersonStatistics {
        personAnalyticsPolicy.checkInterval(from, to)

        val target =
            personRepository.findById(personId)
                ?: throw AnalyticsNotFoundException("Person not found.")

        personAnalyticsPolicy.checkCanView(caller, target)

        val reports = coderReportRepository.findByCoderIdAndReportDateBetween(target.id, from, to)

        val points = buildPoints(reports, granularity)
        val totals = reports.fold(ActivityMetrics.ZERO) { acc, r -> acc + r.metrics }

        return PersonStatistics(
            person = target,
            totals = totals,
            points = points,
            granularity = granularity,
        )
    }

    private fun buildPoints(
        reports: List<CoderReport>,
        granularity: Granularity,
    ): List<ProductivityPoint> =
        when (granularity) {
            Granularity.DAY -> {
                reports.map { report ->
                    ProductivityPoint(
                        periodStart = report.reportDate.atZone(ZoneOffset.UTC).toLocalDate(),
                        metrics = report.metrics,
                    )
                }
            }

            Granularity.WEEK, Granularity.MONTH -> {
                reports
                    .groupBy { toBucketDate(it.reportDate, granularity) }
                    .map { (periodStart, periodReports) ->
                        ProductivityPoint(
                            periodStart = periodStart,
                            metrics = periodReports.fold(ActivityMetrics.ZERO) { acc, r -> acc + r.metrics },
                        )
                    }
            }
        }

    private fun toBucketDate(
        date: Instant,
        granularity: Granularity,
    ): LocalDate {
        val localDate = date.atZone(ZoneOffset.UTC).toLocalDate()
        return when (granularity) {
            Granularity.DAY -> localDate
            Granularity.WEEK -> localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            Granularity.MONTH -> localDate.withDayOfMonth(1)
        }
    }
}
