package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.exception.AnalyticsBadRequestException
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Team
import java.time.Instant

@Component
class HiringAnalyticsPolicy {
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
        team: Team?,
    ) {
        if (caller.role != PersonRole.MANAGER) {
            throw AccessDeniedException("Access is denied. Only managers can view hiring analytics.")
        }

        val companyId =
            caller.companyId
                ?: throw AccessDeniedException("Access is denied. Manager is not assigned to a company.")

        if (team != null && team.companyId != companyId) {
            throw AccessDeniedException("Access is denied. Team belongs to another company.")
        }
    }
}
