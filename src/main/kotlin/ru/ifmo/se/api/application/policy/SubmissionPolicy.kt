package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Submission
import ru.ifmo.se.api.domain.model.SubmissionFilter

@Component
class SubmissionPolicy {
    fun applyFilterScope(
        caller: Person,
        filter: SubmissionFilter,
    ): SubmissionFilter? =
        when (caller.role) {
            PersonRole.ADMIN -> {
                filter
            }

            PersonRole.CANDIDATE -> {
                filter.copy(senderId = caller.id)
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
        submission: Submission,
        contestCompanyIds: Set<CompanyId>,
    ) {
        if (caller.role == PersonRole.ADMIN) return

        if (caller.role == PersonRole.CANDIDATE) {
            if (submission.senderId != caller.id) {
                throw AccessDeniedException("Access is denied. Candidates may only view their own submissions.")
            }
            return
        }

        val companyId = caller.companyId
        if (contestCompanyIds.isNotEmpty() && companyId != null) {
            if (companyId !in contestCompanyIds) {
                throw AccessDeniedException("Access is denied. You can only view submissions of your own company.")
            }
        }
    }

    fun checkCanCreate(caller: Person) {
        if (caller.role != PersonRole.CANDIDATE && caller.role != PersonRole.ADMIN) {
            throw AccessDeniedException("Access is denied. Only candidates may submit solutions.")
        }
    }
}
