package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity
import java.util.UUID

interface JpaTeamRepository : JpaRepository<TeamEntity, UUID>
