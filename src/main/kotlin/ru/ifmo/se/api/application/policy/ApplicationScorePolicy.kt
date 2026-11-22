package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.Vacancy
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity

@Component
class ApplicationScorePolicy {
    fun checkCanViewScore(
        caller: Person,
        vacancy: VacancyEntity,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        val companyId = caller.companyId
        if (caller.role == PersonRole.HR || caller.role == PersonRole.MANAGER) {
            if (companyId != null && vacancy.team.company.id == companyId.value) {
                return
            }
            throw AccessDeniedException("Access is denied. Application does not belong to your company.")
        }

        val teamId = caller.teamId
        if (caller.role == PersonRole.TEAM_LEAD) {
            if (teamId != null && vacancy.team.id == teamId.value) {
                return
            }
            throw AccessDeniedException("Access is denied. Application does not belong to your team.")
        }

        throw AccessDeniedException("Access is denied.")
    }

    fun checkCanViewLeaderboard(
        caller: Person,
        vacancy: Vacancy,
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
