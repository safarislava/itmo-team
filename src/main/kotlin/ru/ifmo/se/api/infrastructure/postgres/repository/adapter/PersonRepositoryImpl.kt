package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonFilter
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.repository.PersonRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.PersonEntity
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaPersonRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.adapter.mapper.PersonEntityMapper
import java.util.UUID

@Repository
class PersonRepositoryImpl(
    private val repo: JpaPersonRepository,
    private val mapper: PersonEntityMapper,
) : PersonRepository {
    override fun findById(id: PersonId): Person? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun save(person: Person): Person = mapper.toDomain(repo.save(mapper.toEntity(person)))

    override fun delete(person: Person) {
        repo.deleteById(person.id.value)
    }

    override fun deleteById(id: PersonId): Boolean =
        if (repo.existsById(id.value)) {
            repo.deleteById(id.value)
            true
        } else {
            false
        }

    override fun findByEmail(email: String): Person? = repo.findByEmail(email).map(mapper::toDomain).orElse(null)

    override fun existsByEmail(email: String): Boolean = repo.existsByEmail(email)

    override fun findByGithubId(githubId: Long): Person? = repo.findByGithubId(githubId).map(mapper::toDomain).orElse(null)

    override fun findAllByTeamId(teamId: TeamId): List<Person> = repo.findAllByTeamId(teamId.value).map(mapper::toDomain)

    override fun findAllByCompanyId(companyId: CompanyId): List<Person> = repo.findAllByCompanyId(companyId.value).map(mapper::toDomain)

    override fun findByFilter(
        filter: PersonFilter,
        page: PageQuery,
    ): PageResult<Person> {
        val spec =
            Specification<PersonEntity> { root, _, cb ->
                val predicates = mutableListOf<Predicate>()

                filter.search?.takeIf { it.isNotBlank() }?.let { query ->
                    val pattern = "%${query.trim().lowercase()}%"
                    predicates.add(
                        cb.or(
                            cb.like(cb.lower(root.get("name")), pattern),
                            cb.like(cb.lower(root.get("email")), pattern),
                        ),
                    )
                }

                filter.role?.let {
                    predicates.add(cb.equal(root.get<PersonRole>("role"), it))
                }

                filter.companyId?.let {
                    predicates.add(cb.equal(root.get<UUID>("companyId"), it.value))
                }

                filter.teamId?.let {
                    predicates.add(cb.equal(root.get<UUID>("teamId"), it.value))
                }

                cb.and(*predicates.toTypedArray())
            }

        val direction = if (page.sortOrder == SortDirection.DESC) Sort.Direction.DESC else Sort.Direction.ASC
        val sortProperty =
            when (page.sortBy) {
                "email" -> "email"
                "name" -> "name"
                "role" -> "role"
                else -> "id"
            }

        val pageable = PageRequest.of(page.pageNumber, page.pageSize, Sort.by(direction, sortProperty))
        val pagedResult = repo.findAll(spec, pageable)

        return PageResult(
            items = pagedResult.content.map(mapper::toDomain),
            totalElements = pagedResult.totalElements,
            pageNumber = pagedResult.number,
            totalPages = pagedResult.totalPages,
        )
    }
}
