package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.CreateContestCommand
import ru.ifmo.se.api.application.dto.UpdateContestCommand
import ru.ifmo.se.api.application.policy.ContestPolicy
import ru.ifmo.se.api.domain.exception.ContestAlreadyStartedException
import ru.ifmo.se.api.domain.exception.ContestNotFoundException
import ru.ifmo.se.api.domain.model.Contest
import ru.ifmo.se.api.domain.model.ContestFilter
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.repository.ContestRepository
import java.time.Instant

@Service
class ContestService(
    private val contestRepository: ContestRepository,
    private val contestPolicy: ContestPolicy,
) {
    fun getContests(
        caller: Person,
        pageQuery: PageQuery,
        filter: ContestFilter,
    ): PageResult<Contest> {
        val scopedFilter =
            contestPolicy.applyFilterScope(caller, filter) ?: return PageResult(
                items = emptyList(),
                totalElements = 0,
                pageNumber = pageQuery.pageNumber,
                totalPages = 0,
            )
        return contestRepository.findAll(scopedFilter, pageQuery)
    }

    fun getContest(
        caller: Person,
        contestId: ContestId,
    ): Contest {
        val contest = contestRepository.findById(contestId) ?: throw ContestNotFoundException()
        val isParticipant = contestRepository.isParticipant(contestId, caller.id)
        val companyIds = contestRepository.findCompanyIdsByContestId(contestId)
        contestPolicy.checkCanView(caller, isParticipant, companyIds)
        return contest
    }

    @Transactional
    fun createContest(
        caller: Person,
        command: CreateContestCommand,
    ): Contest {
        val contest =
            Contest(
                id = ContestId.generate(),
                name = command.name,
                scheduledTime = command.scheduledTime,
                duration = command.duration,
            )
        return contestRepository.save(contest)
    }

    @Transactional
    fun updateContest(
        caller: Person,
        contestId: ContestId,
        command: UpdateContestCommand,
    ): Contest {
        val contest = contestRepository.findById(contestId) ?: throw ContestNotFoundException()
        val companyIds = contestRepository.findCompanyIdsByContestId(contestId)
        contestPolicy.checkCanModify(caller, companyIds)

        val now = Instant.now()
        if (!now.isBefore(contest.scheduledTime)) {
            throw ContestAlreadyStartedException()
        }

        val updated =
            contest.copy(
                name = command.name,
                scheduledTime = command.scheduledTime,
                duration = command.duration,
            )
        return contestRepository.save(updated)
    }

    @Transactional
    fun deleteContest(
        caller: Person,
        contestId: ContestId,
    ) {
        val contest = contestRepository.findById(contestId) ?: throw ContestNotFoundException()
        val companyIds = contestRepository.findCompanyIdsByContestId(contestId)
        contestPolicy.checkCanModify(caller, companyIds)

        contestRepository.delete(contestId)
    }
}
