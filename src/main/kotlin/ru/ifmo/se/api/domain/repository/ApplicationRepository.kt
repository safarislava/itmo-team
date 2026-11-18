package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.Application
import ru.ifmo.se.api.domain.model.ApplicationFilter
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.VacancyId

interface ApplicationRepository {
    fun findById(id: ApplicationId): Application?

    fun findByCandidateIdAndVacancyId(
        candidateId: PersonId,
        vacancyId: VacancyId,
    ): Application?

    fun findAll(
        filter: ApplicationFilter,
        pageQuery: PageQuery,
    ): PageResult<Application>

    fun save(application: Application): Application

    fun delete(application: Application)
}
