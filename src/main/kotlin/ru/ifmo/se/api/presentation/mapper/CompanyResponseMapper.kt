package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Company
import ru.ifmo.se.api.presentation.dto.response.CompanyResponse
import ru.ifmo.se.api.presentation.dto.response.CompanySummaryResponse

@Component
class CompanyResponseMapper {
    fun toResponse(company: Company): CompanyResponse =
        CompanyResponse(
            id = company.id.value,
            name = company.name,
            creationDate = company.creationDate,
        )

    fun toSummaryResponse(company: Company): CompanySummaryResponse =
        CompanySummaryResponse(
            id = company.id.value,
            name = company.name,
            creationDate = company.creationDate,
        )
}
