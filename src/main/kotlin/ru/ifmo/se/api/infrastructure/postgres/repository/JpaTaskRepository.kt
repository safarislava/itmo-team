package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import ru.ifmo.se.api.infrastructure.postgres.entity.TaskEntity
import java.util.UUID

interface JpaTaskRepository :
    JpaRepository<TaskEntity, UUID>,
    JpaSpecificationExecutor<TaskEntity>
