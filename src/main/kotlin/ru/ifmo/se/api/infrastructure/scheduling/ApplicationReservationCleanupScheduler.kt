package ru.ifmo.se.api.infrastructure.scheduling

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.service.ApplicationReservationService
import java.util.concurrent.TimeUnit

@Component
class ApplicationReservationCleanupScheduler(
    private val reservationService: ApplicationReservationService,
) {
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    fun cleanupExpiredReservations() {
        reservationService.cleanExpired()
    }
}
