package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.TeamFilter
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.repository.TeamRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.CompanyEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity
import ru.ifmo.se.api.infrastructure.postgres.mapper.TeamEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaTeamRepository
import java.util.UUID

@Repository
class TeamRepositoryImpl(
    private val repo: JpaTeamRepository,
    private val mapper: TeamEntityMapper,
) : TeamRepository {
    override fun findById(id: TeamId): Team? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

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

        val direction = if (pageQuery.sortOrder == SortDirection.DESC) Sort.Direction.DESC else Sort.Direction.ASC
        val sortProperty =
            when (pageQuery.sortBy) {
                "name" -> "name"
                else -> "id"
            }

        val pageable = PageRequest.of(pageQuery.pageNumber, pageQuery.pageSize, Sort.by(direction, sortProperty))
        val pagedResult = repo.findAll(specification, pageable)

        return PageResult(
            items = pagedResult.content.map(mapper::toDomain),
            totalElements = pagedResult.totalElements,
            pageNumber = pagedResult.number,
            totalPages = pagedResult.totalPages,
        )
    }

    override fun save(team: Team): Team = mapper.toDomain(repo.save(mapper.toEntity(team)))

    override fun delete(id: TeamId) = repo.deleteById(id.value)
}
