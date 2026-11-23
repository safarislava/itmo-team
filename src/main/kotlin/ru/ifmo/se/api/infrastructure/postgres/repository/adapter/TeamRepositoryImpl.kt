package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.TeamFilter
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.repository.TeamRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.CompanyEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageRequest
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageResult
import ru.ifmo.se.api.infrastructure.postgres.mapper.TeamEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaTeamRepository
import java.util.UUID

@Repository
class TeamRepositoryImpl(
    private val repo: JpaTeamRepository,
    private val mapper: TeamEntityMapper,
) : TeamRepository {
    override fun findById(id: TeamId): Team? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAllById(ids: Iterable<TeamId>): List<Team> =
        ids.map { it.value }.takeIf { it.isNotEmpty() }?.let { repo.findAllById(it).map(mapper::toDomain) } ?: emptyList()

    override fun findByTeamleadId(teamleadId: PersonId): Team? = repo.findByTeamleadId(teamleadId.value).map(mapper::toDomain).orElse(null)

    override fun findAll(
        filter: TeamFilter,
        pageQuery: PageQuery,
    ): PageResult<Team> {
        val specification =
            Specification<TeamEntity> { root, _, cb ->
                val predicates = mutableListOf<Predicate>()

                filter.companyId?.let {
                    predicates.add(cb.equal(root.get<CompanyEntity>("company").get<UUID>("id"), it.value))
                }

                filter.name?.takeIf { it.isNotBlank() }?.let { nameQuery ->
                    val pattern = "%${nameQuery.trim().lowercase()}%"
                    predicates.add(cb.like(cb.lower(root.get("name")), pattern))
                }

                cb.and(*predicates.toTypedArray())
            }

        val pageable = pageQuery.toPageRequest { if (it == "name") "name" else "id" }
        return repo.findAll(specification, pageable).toPageResult(mapper::toDomain)
    }

    override fun save(team: Team): Team = mapper.toDomain(repo.save(mapper.toEntity(team)))

    override fun delete(id: TeamId) = repo.deleteById(id.value)
}
