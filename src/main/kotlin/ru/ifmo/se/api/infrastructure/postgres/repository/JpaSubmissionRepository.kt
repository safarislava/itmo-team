package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.ifmo.se.api.infrastructure.postgres.entity.SubmissionEntity
import java.time.Instant
import java.util.Optional
import java.util.UUID

interface JpaSubmissionRepository :
    JpaRepository<SubmissionEntity, UUID>,
    JpaSpecificationExecutor<SubmissionEntity> {
    @EntityGraph(attributePaths = ["response"])
    override fun findById(id: UUID): Optional<SubmissionEntity>

    @EntityGraph(attributePaths = ["response"])
    override fun findAll(
        spec: Specification<SubmissionEntity>,
        pageable: Pageable,
    ): Page<SubmissionEntity>

    fun countBySenderIdAndContestIdAndTaskId(
        senderId: UUID,
        contestId: UUID,
        taskId: UUID,
    ): Long

    @Modifying
    @Query("UPDATE SubmissionEntity s SET s.aiScore = :aiScore WHERE s.id = :id")
    fun updateAiScore(
        @Param("id") id: UUID,
        @Param("aiScore") aiScore: Short,
    )

    @Query(
        """
        SELECT s.id
        FROM SubmissionEntity s
        WHERE s.status = ru.ifmo.se.api.domain.model.SubmissionStatus.QUEUED
           OR (
               s.status = ru.ifmo.se.api.domain.model.SubmissionStatus.RUNNING
               AND s.leaseUntil < :now
           )
        ORDER BY s.submittedAt ASC
        """,
    )
    fun findPendingQueueIds(
        @Param("now") now: Instant,
        pageable: Pageable,
    ): List<UUID>
}
