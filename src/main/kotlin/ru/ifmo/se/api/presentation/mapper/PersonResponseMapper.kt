package ru.ifmo.se.api.presentation.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.repository.CompanyRepository
import ru.ifmo.se.api.domain.repository.TeamRepository
import ru.ifmo.se.api.presentation.dto.response.PersonResponse
import ru.ifmo.se.api.presentation.dto.response.PersonSummaryResponse

@Component
class PersonResponseMapper(
    private val companyRepository: CompanyRepository,
    private val teamRepository: TeamRepository,
    private val companyResponseMapper: CompanyResponseMapper,
    private val teamResponseMapper: TeamResponseMapper,
) {
    fun toResponse(person: Person): PersonResponse {
        val company =
            person.companyId
                ?.let { companyRepository.findById(it) }
                ?.let(companyResponseMapper::toSummaryResponse)

        val team =
            person.teamId
                ?.let { teamRepository.findById(it) }
                ?.let(teamResponseMapper::toSummaryResponse)

        return PersonResponse(
            id = person.id.value,
            name = person.name,
            email = person.email,
            role = person.role,
            company = company,
            team = team,
            githubId = person.githubId,
        )
    }

    fun toSummaryResponse(person: Person): ru.ifmo.se.api.presentation.dto.response.PersonSummaryResponse =
        PersonSummaryResponse(
            id = person.id.value,
            name = person.name,
            role = person.role,
        )
}
