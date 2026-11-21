package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Vacancy
import ru.ifmo.se.api.domain.model.VacancyFilter
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity

interface VacancyRepository {
    fun findById(id: VacancyId): Vacancy?

    fun findAll(
        filter: VacancyFilter,
        pageQuery: PageQuery,
    ): PageResult<Vacancy>

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

    fun findEntityById(id: VacancyId): VacancyEntity?
}
