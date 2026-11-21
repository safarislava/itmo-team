package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.infrastructure.postgres.entity.TeamEntity
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaCompanyRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaPersonRepository

@Component
class TeamEntityMapper(
    private val companyRepository: JpaCompanyRepository,
    private val personRepository: JpaPersonRepository,
) {
    fun toDomain(entity: TeamEntity): Team =
        Team(
            id = TeamId(entity.id),
            name = entity.name,
            companyId = CompanyId(entity.company.id),
            teamleadId = PersonId(entity.teamlead.id),
        )

    fun toEntity(domain: Team): TeamEntity =
        TeamEntity(
            id = domain.id.value,
            name = domain.name,
            company = companyRepository.getReferenceById(domain.companyId.value),
            teamlead = personRepository.getReferenceById(domain.teamleadId.value),
        )
}
