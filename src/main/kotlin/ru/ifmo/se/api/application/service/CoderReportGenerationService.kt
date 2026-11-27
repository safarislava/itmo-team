package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.annotation.WriteDataAccess
import ru.ifmo.se.api.application.port.out.GitHubActivityProvider
import ru.ifmo.se.api.domain.model.ActivityMetrics
import ru.ifmo.se.api.domain.model.CoderReport
import ru.ifmo.se.api.domain.model.CoderReportId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.repository.CoderReportRepository
import ru.ifmo.se.api.domain.repository.PersonRepository
import java.time.LocalDate
import java.time.ZoneId

@Service
@WriteDataAccess
class CoderReportGenerationService(
    private val personRepository: PersonRepository,
    private val coderReportRepository: CoderReportRepository,
    private val gitHubActivityProvider: GitHubActivityProvider,
) {
    @Transactional
    fun generateDailyReports(date: LocalDate): List<CoderReport> =
        personRepository.findAllByRole(PersonRole.DEVELOPER).map { dev ->
            val metrics =
                dev.githubUsername?.takeIf { it.isNotBlank() }?.let { username ->
                    runCatching {
                        gitHubActivityProvider.fetchDailyActivity(username, date)
                    }.getOrElse {
                        ActivityMetrics.ZERO
                    }
                } ?: ActivityMetrics.ZERO

            val report =
                CoderReport(
                    id = CoderReportId.generate(),
                    coderId = dev.id,
                    metrics = metrics,
                    reportDate = date,
                )
            coderReportRepository.save(report)
        }

    companion object {
        val ZONE_MOSCOW: ZoneId = ZoneId.of("Europe/Moscow")
    }
}
