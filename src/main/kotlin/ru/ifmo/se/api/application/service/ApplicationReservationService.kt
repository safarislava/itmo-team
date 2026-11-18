package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationReservation
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.repository.ApplicationReservationRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class ApplicationReservationService(
    private val reservationRepository: ApplicationReservationRepository,
) {
    @Transactional
    fun reserve(
        applicationId: ApplicationId,
        hrId: PersonId,
        durationMinutes: Long = 30,
    ): ApplicationReservation {
        val expiresAt = Instant.now().plus(durationMinutes, ChronoUnit.MINUTES)
        val existing = reservationRepository.findByApplicationId(applicationId)
        val reservation =
            existing?.copy(hrId = hrId, expiresAt = expiresAt)
                ?: ApplicationReservation(
                    applicationId = applicationId,
                    hrId = hrId,
                    expiresAt = expiresAt,
                )
        return reservationRepository.save(reservation)
    }

    @Transactional
    fun release(applicationId: ApplicationId) {
        reservationRepository.deleteByApplicationId(applicationId)
    }

    @Transactional
    fun cleanExpired(): Int = reservationRepository.deleteAllExpired(Instant.now())
}
