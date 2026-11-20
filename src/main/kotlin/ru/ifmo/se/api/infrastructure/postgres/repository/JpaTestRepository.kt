package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.TestEntity
import java.util.UUID

interface JpaTestRepository : JpaRepository<TestEntity, UUID> {
    fun findByTaskId(taskId: UUID): List<TestEntity>

    fun findByTaskIdAndIsHiddenFalse(taskId: UUID): List<TestEntity>

    fun deleteByTaskId(taskId: UUID)
}
