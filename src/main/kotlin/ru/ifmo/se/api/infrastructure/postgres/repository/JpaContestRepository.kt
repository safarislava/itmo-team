package ru.ifmo.se.api.infrastructure.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import ru.ifmo.se.api.infrastructure.postgres.entity.ContestEntity
import java.util.UUID

interface JpaContestRepository :
    JpaRepository<ContestEntity, UUID>,
    JpaSpecificationExecutor<ContestEntity> {
    fun findByParticipantsIdAndTasksId(
        candidateId: UUID,
        taskId: UUID,
    ): List<ContestEntity>

    fun existsByIdAndParticipantsId(
        id: UUID,
        candidateId: UUID,
    ): Boolean

    @Query("SELECT DISTINCT v.team.company.id FROM ContestEntity c JOIN c.vacancies v WHERE c.id = :contestId")
    fun findCompanyIdsByContestId(contestId: UUID): List<UUID>
}
