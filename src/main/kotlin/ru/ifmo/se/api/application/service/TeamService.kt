package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.CreateTeamCommand
import ru.ifmo.se.api.application.dto.TeamDetails
import ru.ifmo.se.api.application.dto.UpdateTeamCommand
import ru.ifmo.se.api.application.policy.TeamPolicy
import ru.ifmo.se.api.domain.exception.CompanyNotFoundException
import ru.ifmo.se.api.domain.exception.InvalidTeamMemberException
import ru.ifmo.se.api.domain.exception.PersonNotFoundException
import ru.ifmo.se.api.domain.exception.TeamBadRequestException
import ru.ifmo.se.api.domain.exception.TeamNotFoundException
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.TeamFilter
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.model.map
import ru.ifmo.se.api.domain.repository.CompanyRepository
import ru.ifmo.se.api.domain.repository.PersonRepository
import ru.ifmo.se.api.domain.repository.TeamRepository

@Service
class TeamService(
    private val teamRepository: TeamRepository,
    private val companyRepository: CompanyRepository,
    private val personRepository: PersonRepository,
    private val teamPolicy: TeamPolicy,
) {
    fun listTeams(
        caller: Person,
        filter: TeamFilter,
        pageQuery: PageQuery,
    ): PageResult<TeamDetails> {
        val scopedFilter =
            teamPolicy.applyFilterScope(caller, filter) ?: return PageResult.empty(pageQuery.pageNumber)
        val pagedTeams = teamRepository.findAll(scopedFilter, pageQuery)
        val companiesById = companyRepository.findAllById(pagedTeams.items.map { it.companyId }).associateBy { it.id }
        val teamleadsById = personRepository.findAllById(pagedTeams.items.map { it.teamleadId }).associateBy { it.id }

        return pagedTeams.map { team ->
            val company = companiesById[team.companyId] ?: throw CompanyNotFoundException()
            val teamlead = teamleadsById[team.teamleadId] ?: throw PersonNotFoundException()
            TeamDetails(team = team, company = company, teamlead = teamlead)
        }
    }

    fun getTeam(
        caller: Person,
        id: TeamId,
    ): TeamDetails {
        val team = teamRepository.findById(id) ?: throw TeamNotFoundException()
        teamPolicy.checkCanView(caller, team)
        val company = companyRepository.findById(team.companyId) ?: throw CompanyNotFoundException()
        val teamlead = personRepository.findById(team.teamleadId) ?: throw PersonNotFoundException()
        return TeamDetails(team = team, company = company, teamlead = teamlead)
    }

    @Transactional
    fun createTeam(
        caller: Person,
        command: CreateTeamCommand,
    ): TeamDetails {
        val companyId =
            if (caller.role == PersonRole.ADMIN) {
                command.companyId ?: throw TeamBadRequestException("companyId is required for administrators.")
            } else {
                caller.companyId ?: throw TeamBadRequestException("HR has no company assigned.")
            }

        teamPolicy.checkCanManage(caller, companyId)

        val company = companyRepository.findById(companyId) ?: throw CompanyNotFoundException()
        val teamlead = personRepository.findById(command.teamleadId) ?: throw PersonNotFoundException()

        if (teamlead.role != PersonRole.TEAM_LEAD ||
            (teamlead.companyId != null && teamlead.companyId != companyId) ||
            teamRepository.findByTeamleadId(teamlead.id) != null
        ) {
            throw InvalidTeamMemberException("The person is a candidate, belongs to another company or leads another team.")
        }

        val team =
            Team(
                id = TeamId.generate(),
                name = command.name.trim(),
                companyId = companyId,
                teamleadId = teamlead.id,
            )
        val savedTeam = teamRepository.save(team)
        val savedTeamlead = personRepository.save(teamlead.copy(companyId = companyId, teamId = savedTeam.id))

        return TeamDetails(team = savedTeam, company = company, teamlead = savedTeamlead)
    }

    @Transactional
    fun updateTeam(
        caller: Person,
        id: TeamId,
        command: UpdateTeamCommand,
    ): TeamDetails {
        val existing = teamRepository.findById(id) ?: throw TeamNotFoundException()
        teamPolicy.checkCanManage(caller, existing.companyId)

        val company = companyRepository.findById(existing.companyId) ?: throw CompanyNotFoundException()
        val trimmedName = command.name.trim()

        val targetTeamleadId = command.teamleadId
        val teamlead =
            if (targetTeamleadId != null && targetTeamleadId != existing.teamleadId) {
                val newLead = personRepository.findById(targetTeamleadId) ?: throw PersonNotFoundException()
                if (newLead.role != PersonRole.TEAM_LEAD ||
                    (newLead.companyId != null && newLead.companyId != existing.companyId) ||
                    teamRepository.findByTeamleadId(newLead.id) != null
                ) {
                    throw InvalidTeamMemberException(
                        "The person is a candidate, belongs to another company or leads another team.",
                    )
                }

                personRepository.findById(existing.teamleadId)?.let { oldLead ->
                    if (oldLead.teamId == existing.id) {
                        personRepository.save(oldLead.copy(teamId = null))
                    }
                }

                personRepository.save(newLead.copy(companyId = existing.companyId, teamId = existing.id))
            } else {
                personRepository.findById(existing.teamleadId) ?: throw PersonNotFoundException()
            }

        val updatedTeam = teamRepository.save(existing.copy(name = trimmedName, teamleadId = teamlead.id))
        return TeamDetails(team = updatedTeam, company = company, teamlead = teamlead)
    }

    @Transactional
    fun deleteTeam(
        caller: Person,
        id: TeamId,
    ) {
        val team = teamRepository.findById(id) ?: throw TeamNotFoundException()
        teamPolicy.checkCanManage(caller, team.companyId)

        personRepository.findAllByTeamId(id).forEach { member ->
            personRepository.save(member.copy(teamId = null))
        }

        personRepository.findById(team.teamleadId)?.let { teamlead ->
            if (teamlead.teamId == id) {
                personRepository.save(teamlead.copy(teamId = null))
            }
        }

        teamRepository.delete(id)
    }
}
