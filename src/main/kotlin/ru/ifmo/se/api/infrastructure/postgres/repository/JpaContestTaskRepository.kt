package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.ContestTaskEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.ContestTaskId
import java.util.UUID

interface JpaContestTaskRepository : JpaRepository<ContestTaskEntity, ContestTaskId> {
    fun findByIdContestId(contestId: UUID): List<ContestTaskEntity>

    fun findByIdContestIdAndIdTaskId(
        contestId: UUID,
        taskId: UUID,
    ): ContestTaskEntity?

    fun deleteByIdContestIdAndIdTaskId(
        contestId: UUID,
        taskId: UUID,
    )
}
