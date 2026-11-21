package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.RunnerServerEntity
import java.util.UUID

interface JpaRunnerServerRepository : JpaRepository<RunnerServerEntity, UUID>
