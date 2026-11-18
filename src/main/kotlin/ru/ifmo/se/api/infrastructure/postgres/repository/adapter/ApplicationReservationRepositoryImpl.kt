package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationReservation
import ru.ifmo.se.api.domain.repository.ApplicationReservationRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.ApplicationReservationEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaApplicationReservationRepository
import java.time.Instant

@Repository
class ApplicationReservationRepositoryImpl(
    private val repo: JpaApplicationReservationRepository,
    private val mapper: ApplicationReservationEntityMapper,
) : ApplicationReservationRepository {
    override fun findByApplicationId(applicationId: ApplicationId): ApplicationReservation? =
        repo.findByApplicationId(applicationId.value)?.let(mapper::toDomain)

    override fun save(reservation: ApplicationReservation): ApplicationReservation =
        mapper.toDomain(repo.save(mapper.toEntity(reservation)))

    override fun deleteByApplicationId(applicationId: ApplicationId) {
        repo.deleteByApplicationId(applicationId.value)
    }

    override fun deleteAllExpired(now: Instant): Int = repo.deleteByExpiresAtBefore(now)
}
