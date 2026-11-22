package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Team

@Component
class ApplicationScorePolicy {
    fun checkCanViewScore(
        caller: Person,
        team: Team,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        val companyId = caller.companyId
        if (caller.role == PersonRole.HR || caller.role == PersonRole.MANAGER) {
            if (companyId != null && team.companyId == companyId) {
                return
            }
            throw AccessDeniedException("Access is denied. Application does not belong to your company.")
        }

        val teamId = caller.teamId
        if (caller.role == PersonRole.TEAM_LEAD) {
            if (teamId != null && team.id == teamId) {
                return
            }
            throw AccessDeniedException("Access is denied. Application does not belong to your team.")
        }

        throw AccessDeniedException("Access is denied.")
    }

    fun checkCanViewLeaderboard(
        caller: Person,
        team: Team,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role == PersonRole.TEAM_LEAD) {
            if (caller.id == team.teamleadId && caller.teamId == team.id) {
                return
            }
            throw AccessDeniedException("Only the team lead of the team of the vacancy may view its leaderboard.")
        }

        if (caller.role == PersonRole.HR) {
            if (caller.companyId == team.companyId) {
                return
            }
            throw AccessDeniedException("Only an HR of the company of the vacancy may view its leaderboard.")
        }

        throw AccessDeniedException("Access is denied.")
    }
}
