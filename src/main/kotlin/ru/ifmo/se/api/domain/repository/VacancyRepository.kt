package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Vacancy
import ru.ifmo.se.api.domain.model.VacancyFilter
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.domain.model.VacancySummary

interface VacancyRepository {
    fun findById(id: VacancyId): Vacancy?

    fun findAllById(ids: Iterable<VacancyId>): List<Vacancy>

    fun findAllSummaries(
        filter: VacancyFilter,
        pageQuery: PageQuery,
    ): PageResult<VacancySummary>

    fun save(vacancy: Vacancy): Vacancy

    fun delete(id: VacancyId)

    fun attachContest(
        vacancyId: VacancyId,
        contestId: ContestId,
    )

    fun detachContest(
        vacancyId: VacancyId,
        contestId: ContestId,
    )

    fun isContestAttached(
        vacancyId: VacancyId,
        contestId: ContestId,
    ): Boolean

    fun countContests(vacancyId: VacancyId): Long
}
