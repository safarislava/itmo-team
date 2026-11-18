package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.exception.ApplicationResponseAccessDeniedException
import ru.ifmo.se.api.domain.model.ApplicationResponse
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity

@Component
class ApplicationResponsePolicy {
    fun checkCanView(
        caller: Person,
        vacancy: VacancyEntity,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        val companyId = caller.companyId
        if ((caller.role == PersonRole.HR || caller.role == PersonRole.MANAGER) &&
            companyId != null &&
            vacancy.team.company.id == companyId.value
        ) {
            return
        }

        val teamId = caller.teamId
        if (caller.role == PersonRole.TEAM_LEAD &&
            teamId != null &&
            vacancy.team.id == teamId.value
        ) {
            return
        }

        throw AccessDeniedException("Access is denied. You do not have permission to view responses for this application.")
    }

    fun checkCanCreate(
        caller: Person,
        vacancy: VacancyEntity,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        val companyId = caller.companyId
        if (caller.role == PersonRole.HR && companyId != null && vacancy.team.company.id == companyId.value) {
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
