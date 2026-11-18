package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import ru.ifmo.se.api.infrastructure.postgres.entity.ApplicationEntity
import java.util.UUID

interface JpaApplicationRepository :
    JpaRepository<ApplicationEntity, UUID>,
    JpaSpecificationExecutor<ApplicationEntity> {
    fun findByCandidateIdAndVacancyId(
        candidateId: UUID,
        vacancyId: UUID,
    ): ApplicationEntity?
}
