package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.ifmo.se.api.infrastructure.postgres.entity.ContestEntity
import java.util.UUID

interface JpaContestRepository :
    JpaRepository<ContestEntity, UUID>,
    JpaSpecificationExecutor<ContestEntity> {
    fun findByParticipantsIdAndTasksId(
        candidateId: UUID,
        taskId: UUID,
    ): List<ContestEntity>

    fun findAllByVacanciesIdOrderByScheduledTimeAsc(vacancyId: UUID): List<ContestEntity>

    @Modifying
    @Query(
        value = "INSERT INTO contest_participant (contest_id, person_id) VALUES (:contestId, :personId) ON CONFLICT DO NOTHING",
        nativeQuery = true,
    )
    fun addParticipant(
        @Param("contestId") contestId: UUID,
        @Param("personId") personId: UUID,
    )
}
