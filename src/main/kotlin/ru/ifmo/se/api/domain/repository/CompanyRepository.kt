package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.Company
import ru.ifmo.se.api.domain.model.CompanyFilter
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult

interface CompanyRepository {
    fun findById(id: CompanyId): Company?

    fun findAll(
        filter: CompanyFilter,
        pageQuery: PageQuery,
    ): PageResult<Company>

    fun save(company: Company): Company

    fun delete(company: Company)
}
