package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.exception.ApplicationResponseAccessDeniedException
import ru.ifmo.se.api.domain.model.ApplicationResponse
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.Vacancy

@Component
class ApplicationResponsePolicy {
    fun checkCanView(
        caller: Person,
        vacancy: Vacancy,
        team: Team,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        val companyId = caller.companyId
        if ((caller.role == PersonRole.HR || caller.role == PersonRole.MANAGER) &&
            companyId != null &&
            team.companyId == companyId
        ) {
            return
        }

        val teamId = caller.teamId
        if (caller.role == PersonRole.TEAM_LEAD &&
            teamId != null &&
            vacancy.teamId == teamId
        ) {
            return
        }

        throw AccessDeniedException("Access is denied. You do not have permission to view responses for this application.")
    }

    fun checkCanCreate(
        caller: Person,
        vacancy: Vacancy,
        team: Team,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        val companyId = caller.companyId
        if (caller.role == PersonRole.HR && companyId != null && team.companyId == companyId) {
            return
        }

        throw AccessDeniedException("Access is denied. Application does not belong to your company.")
    }

    fun checkCanModify(
        caller: Person,
        response: ApplicationResponse,
    ) {
        if (caller.role == PersonRole.ADMIN || response.hrId == caller.id) {
            return
        }
        throw ApplicationResponseAccessDeniedException()
    }
}
