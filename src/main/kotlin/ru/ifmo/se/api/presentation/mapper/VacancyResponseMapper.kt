package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.dto.VacancyDetails
import ru.ifmo.se.api.application.dto.VacancyStatusResultDto
import ru.ifmo.se.api.domain.model.VacancySummary
import ru.ifmo.se.api.presentation.dto.response.CompanyResponse
import ru.ifmo.se.api.presentation.dto.response.SkillResponse
import ru.ifmo.se.api.presentation.dto.response.TeamSummaryResponse
import ru.ifmo.se.api.presentation.dto.response.VacancyListItemResponse
import ru.ifmo.se.api.presentation.dto.response.VacancyResponse
import ru.ifmo.se.api.presentation.dto.response.VacancyStatusResponse

@Component
class VacancyResponseMapper {
    fun toSummaryResponse(summary: VacancySummary): VacancyListItemResponse =
        VacancyListItemResponse(
            id = summary.id.value,
            title = summary.title,
            city = summary.city,
            salary = summary.salary,
            status = summary.status,
            createdAt = summary.createdAt,
            companyId = summary.companyId.value,
            companyName = summary.companyName,
        )

    fun toResponse(details: VacancyDetails): VacancyResponse =
        VacancyResponse(
            id = details.vacancy.id.value,
            title = details.vacancy.title,
            description = details.vacancy.description,
            team =
                TeamSummaryResponse(
                    id = details.team.id.value,
                    name = details.team.name,
                ),
            company =
                CompanyResponse(
                    id = details.company.id.value,
                    name = details.company.name,
                    creationDate = details.company.creationDate,
                ),
            city = details.vacancy.city,
            status = details.vacancy.status,
            salary = details.vacancy.salary,
            skills =
                details.skills.map {
                    SkillResponse(
                        id = it.id.value,
                        name = it.name,
                        description = it.description,
                    )
                },
            createdAt = details.vacancy.createdAt,
            closedAt = details.vacancy.closedAt,
        )

    fun toStatusResponse(result: VacancyStatusResultDto): VacancyStatusResponse =
        VacancyStatusResponse(
            vacancy = toResponse(result.details),
            rejectedApplicationIds = result.rejectedApplicationIds.map { it.value },
        )
}
