package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.exception.AnalyticsBadRequestException
import ru.ifmo.se.api.domain.model.CompanyId
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

    fun resolveCompanyId(
        caller: Person,
        requestedCompanyId: CompanyId?,
    ): CompanyId {
        if (caller.role != PersonRole.MANAGER) {
            throw AccessDeniedException("Access is denied. Only managers can view hiring analytics.")
        }

        val managerCompanyId =
            caller.companyId
                ?: throw AccessDeniedException("Access is denied. Manager is not assigned to a company.")

        if (requestedCompanyId != null && requestedCompanyId != managerCompanyId) {
            throw AccessDeniedException("Access is denied. Cannot view hiring analytics of another company.")
        }

        return managerCompanyId
    }

    fun checkCanView(
        caller: Person,
        companyId: CompanyId,
        team: Team?,
    ) {
        if (caller.role != PersonRole.MANAGER) {
            throw AccessDeniedException("Access is denied. Only managers can view hiring analytics.")
        }

        if (caller.companyId != companyId) {
            throw AccessDeniedException("Access is denied. Cannot view hiring analytics of another company.")
        }

        if (team != null && team.companyId != companyId) {
            throw AccessDeniedException("Access is denied. Team belongs to another company.")
        }
    }
}
