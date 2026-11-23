package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.annotation.ReadOnlyDataAccess
import ru.ifmo.se.api.application.annotation.WriteDataAccess
import ru.ifmo.se.api.application.policy.TeamMemberPolicy
import ru.ifmo.se.api.domain.exception.InvalidTeamMemberException
import ru.ifmo.se.api.domain.exception.PersonNotFoundException
import ru.ifmo.se.api.domain.exception.TeamLeadCannotBeRemovedException
import ru.ifmo.se.api.domain.exception.TeamNotFoundException
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.repository.PersonRepository
import ru.ifmo.se.api.domain.repository.TeamRepository

@Service
class TeamMemberService(
    private val teamRepository: TeamRepository,
    private val personRepository: PersonRepository,
    private val teamMemberPolicy: TeamMemberPolicy,
) {
    @ReadOnlyDataAccess
    fun listTeamMembers(
        caller: Person,
        teamId: TeamId,
        role: PersonRole?,
    ): List<Person> {
        val team = teamRepository.findById(teamId) ?: throw TeamNotFoundException()
        teamMemberPolicy.checkCanListMembers(caller, team)

        val members = personRepository.findAllByTeamId(teamId)
        return members
            .filter { role == null || it.role == role }
            .sortedBy { it.name }
    }

    @WriteDataAccess
    @Transactional
    fun addTeamMember(
        caller: Person,
        teamId: TeamId,
        personId: PersonId,
    ): Person {
        val team = teamRepository.findById(teamId) ?: throw TeamNotFoundException()
        teamMemberPolicy.checkCanManageMembers(caller, team)

        val person = personRepository.findById(personId) ?: throw PersonNotFoundException()

        if (person.role == PersonRole.CANDIDATE) {
            throw InvalidTeamMemberException("The person is a candidate, belongs to another company or leads another team.")
        }
        if (person.companyId != null && person.companyId != team.companyId) {
            throw InvalidTeamMemberException("The person is a candidate, belongs to another company or leads another team.")
        }

        val leadTeam = teamRepository.findByTeamleadId(person.id)
        if (leadTeam != null && leadTeam.id != team.id) {
            throw InvalidTeamMemberException("The person is a candidate, belongs to another company or leads another team.")
        }

        return personRepository.save(person.copy(companyId = team.companyId, teamId = team.id))
    }

    @WriteDataAccess
    @Transactional
    fun removeTeamMember(
        caller: Person,
        teamId: TeamId,
        personId: PersonId,
    ) {
        val team = teamRepository.findById(teamId) ?: throw TeamNotFoundException()
        teamMemberPolicy.checkCanManageMembers(caller, team)

        val person = personRepository.findById(personId) ?: throw PersonNotFoundException()

        if (person.teamId != team.id) {
            throw PersonNotFoundException("Person is not a member of this team.")
        }
        if (team.teamleadId == person.id) {
            throw TeamLeadCannotBeRemovedException()
        }

        personRepository.save(person.copy(teamId = null))
    }
}
