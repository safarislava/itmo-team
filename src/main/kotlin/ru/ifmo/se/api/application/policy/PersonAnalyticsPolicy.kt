package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.exception.AnalyticsBadRequestException
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import java.time.Instant

@Component
class PersonAnalyticsPolicy {
    fun checkInterval(
        from: Instant,
        to: Instant,
    ) {
        if (!from.isBefore(to)) {
            throw AnalyticsBadRequestException("'from' must be strictly before 'to'.")
        }
    }

    fun checkCanView(
        caller: Person,
        target: Person,
    ) {
        when (caller.role) {
            PersonRole.DEVELOPER -> {
                if (caller.id != target.id) {
                    throw AccessDeniedException("Developers can only view their own statistics.")
                }
            }

            PersonRole.TEAM_LEAD -> {
                if (caller.id != target.id && (caller.teamId == null || caller.teamId != target.teamId)) {
                    throw AccessDeniedException("Team leads can only view statistics of their team members.")
                }
            }

            PersonRole.MANAGER -> {
                if (caller.companyId == null || caller.companyId != target.companyId) {
                    throw AccessDeniedException("Managers can only view statistics of their company employees.")
                }
            }

            PersonRole.ADMIN -> {}

            else -> {
                throw AccessDeniedException("Access is denied.")
            }
        }
    }
}
