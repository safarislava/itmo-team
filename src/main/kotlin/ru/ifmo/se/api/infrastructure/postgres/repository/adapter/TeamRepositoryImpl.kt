package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.repository.TeamRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.TeamEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaTeamRepository

@Repository
class TeamRepositoryImpl(
    private val repo: JpaTeamRepository,
    private val mapper: TeamEntityMapper,
) : TeamRepository {
    override fun findById(id: TeamId): Team? = repo.findById(id.value).map(mapper::toDomain).orElse(null)
}
