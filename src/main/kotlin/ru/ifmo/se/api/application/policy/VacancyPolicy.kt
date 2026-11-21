package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.exception.VacancyClosedOrArchivedException
import ru.ifmo.se.api.domain.exception.VacancyInvalidTransitionException
import ru.ifmo.se.api.domain.exception.VacancyNoContestAttachedException
import ru.ifmo.se.api.domain.exception.VacancyNotDraftException
import ru.ifmo.se.api.domain.exception.VacancyNotFoundException
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.Vacancy
import ru.ifmo.se.api.domain.model.VacancyFilter
import ru.ifmo.se.api.domain.model.VacancyStatus

@Component
class VacancyPolicy {
    fun applyFilterScope(
        caller: Person?,
        filter: VacancyFilter,
    ): VacancyFilter? {
        val requestedStatus = filter.status ?: VacancyStatus.OPEN

        if (caller == null || caller.role == PersonRole.CANDIDATE || caller.role == PersonRole.DEVELOPER) {
            if (requestedStatus != VacancyStatus.OPEN) {
                return null
            }
            return filter.copy(status = VacancyStatus.OPEN)
        }

        if (caller.role == PersonRole.ADMIN) {
            return filter.copy(status = requestedStatus)
        }

        val companyId = caller.companyId ?: return null
        if (filter.companyId != null && filter.companyId != companyId) {
            return null
        }

        return if (requestedStatus != VacancyStatus.OPEN) {
            filter.copy(companyId = companyId, status = requestedStatus)
        } else {
            filter.copy(status = VacancyStatus.OPEN)
        }
    }

    fun checkCanView(
        caller: Person?,
        vacancy: Vacancy,
        team: Team,
    ) {
        if (vacancy.status == VacancyStatus.OPEN) {
            return
        }

        if (caller == null) {
            throw VacancyNotFoundException()
        }

        if (caller.role == PersonRole.ADMIN) {
            return
        }

        val internalRoles = setOf(PersonRole.HR, PersonRole.TEAM_LEAD, PersonRole.MANAGER)
        if (caller.role in internalRoles && caller.companyId == team.companyId) {
            return
        }

        throw VacancyNotFoundException()
    }

    fun checkCanCreate(caller: Person) {
        if (caller.role != PersonRole.TEAM_LEAD || caller.teamId == null) {
            throw AccessDeniedException("Only a team lead assigned to a team may create a vacancy.")
        }
    }

    fun checkCanEdit(
        caller: Person,
        vacancy: Vacancy,
        team: Team,
    ) {
        if (vacancy.status == VacancyStatus.CLOSED || vacancy.status == VacancyStatus.ARCHIVE) {
            throw VacancyClosedOrArchivedException()
        }

        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role != PersonRole.TEAM_LEAD || caller.id != team.teamleadId || caller.teamId != team.id) {
            throw AccessDeniedException("Only a team lead of the team of the vacancy may edit it.")
        }
    }

    fun checkCanDelete(
        caller: Person,
        vacancy: Vacancy,
        team: Team,
    ) {
        if (vacancy.status != VacancyStatus.DRAFT) {
            throw VacancyNotDraftException()
        }

        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role != PersonRole.TEAM_LEAD || caller.id != team.teamleadId || caller.teamId != team.id) {
            throw AccessDeniedException("Only a team lead of the team of the vacancy may delete it.")
        }
    }

    fun checkCanChangeStatus(
        caller: Person,
        vacancy: Vacancy,
        team: Team,
        targetStatus: VacancyStatus,
        hasContests: Boolean,
    ) {
        if (vacancy.status == VacancyStatus.CLOSED || vacancy.status == VacancyStatus.ARCHIVE) {
            throw VacancyClosedOrArchivedException()
        }

        when (targetStatus) {
            VacancyStatus.OPEN -> {
                if (vacancy.status != VacancyStatus.DRAFT) {
                    throw VacancyInvalidTransitionException()
                }
                if (!hasContests) {
                    throw VacancyNoContestAttachedException()
                }
                if (caller.role == PersonRole.ADMIN) return
                if (caller.role != PersonRole.TEAM_LEAD || caller.id != team.teamleadId || caller.teamId != team.id) {
                    throw AccessDeniedException("Only a team lead of the team of the vacancy may publish it.")
                }
            }

            VacancyStatus.CLOSED -> {
                if (vacancy.status != VacancyStatus.OPEN) {
                    throw VacancyInvalidTransitionException()
                }
                if (caller.role == PersonRole.ADMIN) return
                val isTeamLead = caller.role == PersonRole.TEAM_LEAD && caller.id == team.teamleadId && caller.teamId == team.id
                val isHr = caller.role == PersonRole.HR && caller.companyId == team.companyId
                if (!isTeamLead && !isHr) {
                    throw AccessDeniedException("Only a team lead of the team or an HR of the company may close it.")
                }
            }

            else -> {
                throw VacancyInvalidTransitionException("Cannot transition to status $targetStatus.")
            }
        }
    }
}
