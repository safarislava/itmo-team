package ru.ifmo.se.api.infrastructure.scheduling

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.service.CoderReportGenerationService
import ru.ifmo.se.api.application.service.CoderReportGenerationService.Companion.ZONE_MOSCOW
import java.time.LocalDate

@Component
class CoderReportScheduler(
    private val coderReportGenerationService: CoderReportGenerationService,
) {
    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Moscow")
    fun generateDailyCoderReports() {
        coderReportGenerationService.generateDailyReports(LocalDate.now(ZONE_MOSCOW).minusDays(1))
    }
}
