package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Interview
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.Vacancy

@Component
class InterviewFeedbackPolicy {
    fun checkCanView(
        caller: Person,
        interview: Interview,
        vacancy: Vacancy,
        team: Team,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role == PersonRole.CANDIDATE) {
            throw AccessDeniedException("Access is denied. The feedback is hidden from the candidate.")
        }

        val companyId = caller.companyId
        if ((caller.role == PersonRole.HR || caller.role == PersonRole.MANAGER) &&
            companyId != null &&
            team.companyId == companyId
        ) {
            return
        }

        val teamId = caller.teamId
        if (caller.role == PersonRole.TEAM_LEAD &&
            (interview.teamleadId == caller.id || (teamId != null && vacancy.teamId == teamId))
        ) {
            return
        }

        throw AccessDeniedException("Access is denied.")
    }

    fun checkCanSave(
        caller: Person,
        interview: Interview,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role == PersonRole.TEAM_LEAD && interview.teamleadId == caller.id) {
            return
        }

        throw AccessDeniedException("Access is denied. Only the team lead who conducts the interview may leave the feedback.")
    }
}
