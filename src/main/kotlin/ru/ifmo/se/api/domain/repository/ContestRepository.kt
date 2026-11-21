package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.Contest
import ru.ifmo.se.api.domain.model.ContestFilter
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.model.VacancyId
import java.time.Instant

interface ContestRepository {
    fun findById(id: ContestId): Contest?

    fun findAll(
        filter: ContestFilter,
        pageQuery: PageQuery,
    ): PageResult<Contest>

    fun save(contest: Contest): Contest

    fun delete(id: ContestId)

    fun isCandidateParticipantOfRunningContestWithTask(
        candidateId: PersonId,
        taskId: TaskId,
        now: Instant,
    ): Boolean

    fun isParticipant(
        contestId: ContestId,
        candidateId: PersonId,
    ): Boolean

    fun findCompanyIdsByContestId(contestId: ContestId): Set<CompanyId>

    fun findByVacancyId(vacancyId: VacancyId): List<Contest>

    fun addParticipant(
        contestId: ContestId,
        personId: PersonId,
    )
}
