package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.Company
import ru.ifmo.se.api.domain.model.CompanyFilter
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.repository.CompanyRepository
import ru.ifmo.se.api.infrastructure.postgres.entity.CompanyEntity
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageRequest
import ru.ifmo.se.api.infrastructure.postgres.extension.toPageResult
import ru.ifmo.se.api.infrastructure.postgres.mapper.CompanyEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaCompanyRepository

@Repository
class CompanyRepositoryImpl(
    private val repo: JpaCompanyRepository,
    private val mapper: CompanyEntityMapper,
) : CompanyRepository {
    override fun findById(id: CompanyId): Company? = repo.findById(id.value).map(mapper::toDomain).orElse(null)

    override fun findAllById(ids: Iterable<CompanyId>): List<Company> =
        ids.map { it.value }.takeIf { it.isNotEmpty() }?.let { repo.findAllById(it).map(mapper::toDomain) } ?: emptyList()

    override fun findAll(
        filter: CompanyFilter,
        pageQuery: PageQuery,
    ): PageResult<Company> {
        val specification =
            Specification<CompanyEntity> { root, _, cb ->
                val predicates = mutableListOf<Predicate>()

                filter.search?.takeIf { it.isNotBlank() }?.let { query ->
                    val pattern = "%${query.trim().lowercase()}%"
                    predicates.add(cb.like(cb.lower(root.get("name")), pattern))
                }

                cb.and(*predicates.toTypedArray())
            }

        val pageable =
            pageQuery.toPageRequest {
                when (it) {
                    "name" -> "name"
                    "creationDate" -> "creationDate"
                    else -> "id"
                }
            }
        return repo.findAll(specification, pageable).toPageResult(mapper::toDomain)
    }

    override fun save(company: Company): Company = mapper.toDomain(repo.save(mapper.toEntity(company)))

    override fun delete(company: Company) {
        repo.deleteById(company.id.value)
    }
}
