package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Company
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.infrastructure.postgres.entity.CompanyEntity

@Component
class CompanyEntityMapper {
    fun toDomain(entity: CompanyEntity): Company =
        Company(
            id = CompanyId(entity.id),
            name = entity.name,
            creationDate = entity.creationDate,
        )

    fun toEntity(company: Company): CompanyEntity =
        CompanyEntity(
            id = company.id.value,
            name = company.name,
            creationDate = company.creationDate,
        )
}
