package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import ru.ifmo.se.api.infrastructure.postgres.entity.SubmissionEntity
import java.util.UUID

interface JpaSubmissionRepository :
    JpaRepository<SubmissionEntity, UUID>,
    JpaSpecificationExecutor<SubmissionEntity> {
    fun countBySenderIdAndContestIdAndTaskId(
        senderId: UUID,
        contestId: UUID,
        taskId: UUID,
    ): Long
}
