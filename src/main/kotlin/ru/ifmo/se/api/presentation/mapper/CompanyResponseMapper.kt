package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Company
import ru.ifmo.se.api.presentation.dto.response.CompanySummaryResponse

@Component
class CompanyResponseMapper {
    fun toSummaryResponse(company: Company): CompanySummaryResponse =
        CompanySummaryResponse(
            id = company.id.value,
            name = company.name,
            creationDate = company.creationDate,
        )
}
