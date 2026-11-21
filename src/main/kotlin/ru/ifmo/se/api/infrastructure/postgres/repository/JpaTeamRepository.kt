package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity
import java.util.Optional
import java.util.UUID

interface JpaTeamRepository :
    JpaRepository<TeamEntity, UUID>,
    JpaSpecificationExecutor<TeamEntity> {
    fun findByTeamleadId(teamleadId: UUID): Optional<TeamEntity>
}
