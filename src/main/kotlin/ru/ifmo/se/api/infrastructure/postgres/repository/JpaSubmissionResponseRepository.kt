package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.SubmissionResponseEntity
import java.util.UUID

interface JpaSubmissionResponseRepository : JpaRepository<SubmissionResponseEntity, UUID> {
    fun findBySubmissionId(submissionId: UUID): SubmissionResponseEntity?

    fun findBySubmissionIdIn(submissionIds: Collection<UUID>): List<SubmissionResponseEntity>
}
