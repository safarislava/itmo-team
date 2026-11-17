package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.presentation.dto.response.TeamSummaryResponse

@Component
class TeamResponseMapper {
    fun toSummaryResponse(team: Team): TeamSummaryResponse =
        TeamSummaryResponse(
            id = team.id.value,
            name = team.name,
        )
}
