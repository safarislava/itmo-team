package ru.ifmo.se.api.infrastructure.postgres.repository.adapter.mapper

import org.mapstruct.Mapper
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.infrastructure.postgres.entity.PersonEntity
import java.util.UUID

@Mapper(componentModel = "spring")
interface PersonEntityMapper {
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

    fun toEntity(domain: Person): PersonEntity

    fun toPersonId(value: UUID): PersonId = PersonId(value)

    fun fromPersonId(id: PersonId): UUID = id.value

    fun toCompanyId(value: UUID?): CompanyId? = value?.let { CompanyId(it) }

    fun fromCompanyId(id: CompanyId?): UUID? = id?.value

    fun toTeamId(value: UUID?): TeamId? = value?.let { TeamId(it) }

    fun fromTeamId(id: TeamId?): UUID? = id?.value
}
