package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity

@Component
class TeamEntityMapper {
    fun toDomain(entity: TeamEntity): Team =
        Team(
            id = TeamId(entity.id),
            name = entity.name,
            companyId = CompanyId(entity.company.id),
            teamleadId = PersonId(entity.teamlead.id),
        )
}
