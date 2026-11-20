package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.Contest
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import java.time.Instant

@Component
class ContestTaskPolicy {
    fun checkCanView(
        caller: Person,
        contest: Contest,
        isParticipant: Boolean,
    ) {
        if (caller.role == PersonRole.ADMIN) return

        if (caller.role == PersonRole.CANDIDATE) {
            if (!isParticipant) {
                throw AccessDeniedException("Access is denied. You are not a participant of this contest.")
            }
            val now = Instant.now()
            if (now.isBefore(contest.scheduledTime) || now.isAfter(contest.endTime)) {
                throw AccessDeniedException("Access is denied. Tasks can be viewed only while the contest is running.")
            }
            return
        }

        if (caller.role !in setOf(PersonRole.HR, PersonRole.TEAM_LEAD, PersonRole.MANAGER)) {
            throw AccessDeniedException("Access is denied.")
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
