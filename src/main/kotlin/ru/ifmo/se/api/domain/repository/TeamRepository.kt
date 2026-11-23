package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.TeamFilter
import ru.ifmo.se.api.domain.model.TeamId

interface TeamRepository {
    fun findById(id: TeamId): Team?

    fun findAllById(ids: Iterable<TeamId>): List<Team>

    fun findByTeamleadId(teamleadId: PersonId): Team?

    fun findAll(
        filter: TeamFilter,
        pageQuery: PageQuery,
    ): PageResult<Team>

    fun save(team: Team): Team

    fun delete(id: TeamId)
}
