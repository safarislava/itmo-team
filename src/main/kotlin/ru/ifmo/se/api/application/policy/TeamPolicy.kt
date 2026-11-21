package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.TeamFilter

@Component
class TeamPolicy {
    fun applyFilterScope(
        caller: Person,
        filter: TeamFilter,
    ): TeamFilter? =
        when (caller.role) {
            PersonRole.ADMIN -> {
                filter
            }

            PersonRole.HR, PersonRole.TEAM_LEAD, PersonRole.DEVELOPER, PersonRole.MANAGER -> {
                val companyId = caller.companyId ?: return null
                filter.copy(companyId = companyId)
            }

            PersonRole.CANDIDATE -> {
                null
            }
        }

    fun checkCanView(
        caller: Person,
        team: Team,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role in ALLOWED_VIEW_ROLES) {
            if (caller.companyId == team.companyId) {
                return
            }
            throw AccessDeniedException("Access is denied. Cannot view teams of another company.")
        }

        throw AccessDeniedException("Access is denied.")
    }

    fun checkCanManage(
        caller: Person,
        companyId: CompanyId,
    ) {
        when (caller.role) {
            PersonRole.ADMIN -> {
                return
            }

            PersonRole.HR -> {
                if (caller.companyId == companyId) {
                    return
                }
                throw AccessDeniedException("Access is denied. Cannot manage teams of another company.")
            }

            else -> {
                throw AccessDeniedException("Access is denied. Only HR and administrators may manage teams.")
            }
        }
    }

    private companion object {
        private val ALLOWED_VIEW_ROLES =
            setOf(
                PersonRole.HR,
                PersonRole.TEAM_LEAD,
                PersonRole.DEVELOPER,
                PersonRole.MANAGER,
                PersonRole.ADMIN,
            )
    }
}
