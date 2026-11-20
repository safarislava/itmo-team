package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import ru.ifmo.se.api.infrastructure.postgres.entity.SubmissionEntity
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
}
