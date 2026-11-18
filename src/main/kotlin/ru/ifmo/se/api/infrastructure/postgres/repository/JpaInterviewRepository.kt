package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.query.Procedure
import ru.ifmo.se.api.infrastructure.postgres.entity.InterviewEntity
import java.time.Instant
import java.util.UUID

interface JpaInterviewRepository :
    JpaRepository<InterviewEntity, UUID>,
    JpaSpecificationExecutor<InterviewEntity> {
    @Procedure(procedureName = "schedule_interview")
    fun scheduleInterview(
        applicationId: UUID,
        scheduledStart: Instant,
        scheduledEnd: Instant,
    )

    fun findByCandidateIdAndVacancyIdAndScheduledStartOrderByScheduledStartDesc(
        candidateId: UUID,
        vacancyId: UUID,
        scheduledStart: Instant,
    ): List<InterviewEntity>

    fun findFirstByCandidateIdAndVacancyIdOrderByScheduledStartDesc(
        candidateId: UUID,
        vacancyId: UUID,
    ): InterviewEntity?
}
