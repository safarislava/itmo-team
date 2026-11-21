package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.dto.TeamDetails
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.presentation.dto.response.PersonSummaryResponse
import ru.ifmo.se.api.presentation.dto.response.TeamResponse
import ru.ifmo.se.api.presentation.dto.response.TeamSummaryResponse

@Component
class TeamResponseMapper(
    private val companyResponseMapper: CompanyResponseMapper,
) {
    fun toSummaryResponse(team: Team): TeamSummaryResponse =
        TeamSummaryResponse(
            id = team.id.value,
            name = team.name,
        )

    fun toResponse(details: TeamDetails): TeamResponse =
        TeamResponse(
            id = details.team.id.value,
            name = details.team.name,
            company = companyResponseMapper.toResponse(details.company),
            teamlead =
                PersonSummaryResponse(
                    id = details.teamlead.id.value,
                    name = details.teamlead.name,
                    role = details.teamlead.role,
                ),
        )
}
