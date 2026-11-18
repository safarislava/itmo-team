package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationReservation
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationReservationEntity

@Component
class ApplicationReservationEntityMapper {
    fun toDomain(entity: ApplicationReservationEntity): ApplicationReservation =
        ApplicationReservation(
            applicationId = ApplicationId(entity.applicationId),
            hrId = PersonId(entity.hrId),
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt,
        )

    fun toEntity(domain: ApplicationReservation): ApplicationReservationEntity =
        ApplicationReservationEntity(
            applicationId = domain.applicationId.value,
            hrId = domain.hrId.value,
            expiresAt = domain.expiresAt,
            createdAt = domain.createdAt,
        )
}
