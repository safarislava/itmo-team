package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationReservation
import java.time.Instant

interface ApplicationReservationRepository {
    fun findByApplicationId(applicationId: ApplicationId): ApplicationReservation?

    fun save(reservation: ApplicationReservation): ApplicationReservation

    fun deleteByApplicationId(applicationId: ApplicationId)

    fun deleteAllExpired(now: Instant): Int
}
