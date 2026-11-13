package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonFilter
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.TeamId

interface PersonRepository {
    fun findById(id: PersonId): Person?

    fun save(person: Person): Person

    fun delete(person: Person)

    fun deleteById(id: PersonId): Boolean

    fun findByEmail(email: String): Person?

    fun existsByEmail(email: String): Boolean

    fun findByGithubId(githubId: Long): Person?

    fun findAllByTeamId(teamId: TeamId): List<Person>

    fun findAllByCompanyId(companyId: CompanyId): List<Person>

    fun findByFilter(
        filter: PersonFilter,
        page: PageQuery,
    ): PageResult<Person>
}
