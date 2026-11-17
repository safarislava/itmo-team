package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.Company
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.repository.CompanyRepository
import ru.ifmo.se.api.infrastructure.postgres.mapper.CompanyEntityMapper
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaCompanyRepository

@Repository
class CompanyRepositoryImpl(
    private val repo: JpaCompanyRepository,
    private val mapper: CompanyEntityMapper,
) : CompanyRepository {
    override fun findById(id: CompanyId): Company? = repo.findById(id.value).map(mapper::toDomain).orElse(null)
}
