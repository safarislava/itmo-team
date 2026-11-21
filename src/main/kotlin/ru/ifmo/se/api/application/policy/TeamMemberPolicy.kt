package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Team

@Component
class TeamMemberPolicy {
    fun checkCanListMembers(
        caller: Person,
        team: Team,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role in ALLOWED_LIST_ROLES) {
            if (caller.companyId == team.companyId) {
                return
            }
            throw AccessDeniedException("Access is denied. Cannot view members of a team from another company.")
        }

        throw AccessDeniedException("Access is denied.")
    }

    fun checkCanManageMembers(
        caller: Person,
        team: Team,
    ) {
        when (caller.role) {
            PersonRole.ADMIN -> {
                return
            }

            PersonRole.HR -> {
                if (caller.companyId == team.companyId) {
                    return
                }
                throw AccessDeniedException("Access is denied. Cannot manage members of a team from another company.")
            }

            else -> {
                throw AccessDeniedException("Access is denied. Only HR and administrators may manage team members.")
            }
        }
    }

    private companion object {
        private val ALLOWED_LIST_ROLES =
            setOf(
                PersonRole.HR,
                PersonRole.TEAM_LEAD,
                PersonRole.MANAGER,
                PersonRole.ADMIN,
            )
    }
}
