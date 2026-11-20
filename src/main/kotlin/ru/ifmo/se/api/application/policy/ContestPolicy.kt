package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.ContestFilter
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole

@Component
class ContestPolicy {
    fun applyFilterScope(
        caller: Person,
        filter: ContestFilter,
    ): ContestFilter? =
        when (caller.role) {
            PersonRole.ADMIN -> {
                filter
            }

            PersonRole.CANDIDATE -> {
                filter.copy(participantId = caller.id)
            }

            PersonRole.HR, PersonRole.TEAM_LEAD, PersonRole.MANAGER -> {
                val companyId = caller.companyId ?: return null
                filter.copy(companyId = companyId)
            }

            PersonRole.DEVELOPER -> {
                null
            }
        }

    fun checkCanView(
        caller: Person,
        isParticipant: Boolean,
        contestCompanyIds: Set<CompanyId>,
    ) {
        if (caller.role == PersonRole.ADMIN) return

        if (caller.role == PersonRole.CANDIDATE) {
            if (!isParticipant) {
                throw AccessDeniedException("Access is denied. Candidates can view only contests they participate in.")
            }
            return
        }

        val companyId = caller.companyId
        if (contestCompanyIds.isNotEmpty() && companyId != null) {
            if (companyId !in contestCompanyIds) {
                throw AccessDeniedException("Access is denied. You can only view contests of your own company.")
            }
        }
    }

    fun checkCanModify(
        caller: Person,
        contestCompanyIds: Set<CompanyId>,
    ) {
        if (caller.role == PersonRole.ADMIN) return

        if (caller.role != PersonRole.TEAM_LEAD) {
            throw AccessDeniedException("Access is denied. Only a team lead or administrator may modify contests.")
        }

        val companyId = caller.companyId
        if (contestCompanyIds.isNotEmpty() && companyId != null) {
            if (companyId !in contestCompanyIds) {
                throw AccessDeniedException("Access is denied. You can only modify contests of your own company.")
            }
        }
    }
}
