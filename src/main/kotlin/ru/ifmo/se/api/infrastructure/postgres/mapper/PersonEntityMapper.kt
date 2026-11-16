package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.infrastructure.postgres.entity.PersonEntity

@Component
class PersonEntityMapper {
    fun toDomain(entity: PersonEntity): Person =
        Person(
            id = PersonId(entity.id),
            name = entity.name,
            email = entity.email,
            passwordHash = entity.passwordHash,
            role = entity.role,
            companyId = entity.companyId?.let { CompanyId(it) },
            teamId = entity.teamId?.let { TeamId(it) },
            githubId = entity.githubId,
        )

    fun toEntity(domain: Person): PersonEntity =
        PersonEntity(
            id = domain.id.value,
            name = domain.name,
            email = domain.email,
            passwordHash = domain.passwordHash,
            role = domain.role,
            companyId = domain.companyId?.value,
            teamId = domain.teamId?.value,
            githubId = domain.githubId,
        )
}
