package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Application
import ru.ifmo.se.api.domain.model.ApplicationFilter
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.Vacancy

@Component
class ApplicationPolicy {
    fun applyFilterScope(
        caller: Person,
        filter: ApplicationFilter,
    ): ApplicationFilter? =
        when (caller.role) {
            PersonRole.ADMIN -> {
                filter
            }

            PersonRole.CANDIDATE -> {
                filter.copy(candidateId = caller.id)
            }

            PersonRole.HR, PersonRole.MANAGER -> {
                val companyId = caller.companyId ?: return null
                filter.copy(companyId = companyId)
            }

            PersonRole.TEAM_LEAD -> {
                val teamId = caller.teamId ?: return null
                filter.copy(teamId = teamId)
            }

            PersonRole.DEVELOPER -> {
                null
            }
        }

    fun checkCanView(
        caller: Person,
        application: Application,
        vacancy: Vacancy,
        team: Team,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role == PersonRole.CANDIDATE) {
            if (application.candidateId != caller.id) {
                throw AccessDeniedException("Access is denied. Candidates can view only their own applications.")
            }
            return
        }

        val companyId = caller.companyId
        if (caller.role == PersonRole.HR || caller.role == PersonRole.MANAGER) {
            if (companyId != null && team.companyId == companyId) {
                return
            }
            throw AccessDeniedException("Access is denied. Application does not belong to your company.")
        }

        val teamId = caller.teamId
        if (caller.role == PersonRole.TEAM_LEAD) {
            if (teamId != null && vacancy.teamId == teamId) {
                return
            }
            throw AccessDeniedException("Access is denied. Application does not belong to your team.")
        }

        throw AccessDeniedException("Access is denied.")
    }

    fun checkCanCreate(caller: Person) {
        if (caller.role != PersonRole.CANDIDATE && caller.role != PersonRole.ADMIN) {
            throw AccessDeniedException("Only candidates can submit applications.")
        }
    }

    fun checkCanUpdateStatus(
        caller: Person,
        vacancy: Vacancy,
        team: Team,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        val companyId = caller.companyId
        if (caller.role == PersonRole.HR) {
            if (companyId != null && team.companyId == companyId) {
                return
            }
            throw AccessDeniedException("Access is denied. You can only update applications of your company.")
        }

        val teamId = caller.teamId
        if (caller.role == PersonRole.TEAM_LEAD) {
            if (teamId != null && vacancy.teamId == teamId) {
                return
            }
            throw AccessDeniedException("Access is denied. You can only update applications of your team.")
        }

        throw AccessDeniedException("Access is denied.")
    }
}
