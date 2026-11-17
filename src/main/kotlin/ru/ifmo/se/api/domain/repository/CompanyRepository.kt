package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.Company
import ru.ifmo.se.api.domain.model.CompanyId

interface CompanyRepository {
    fun findById(id: CompanyId): Company?
}
