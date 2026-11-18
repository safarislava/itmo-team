package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Interview
import ru.ifmo.se.api.domain.model.InterviewFilter
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.infrastructure.postgres.entity.VacancyEntity

@Component
class InterviewPolicy {
    fun applyFilterScope(
        caller: Person,
        filter: InterviewFilter,
    ): InterviewFilter? =
        when (caller.role) {
            PersonRole.ADMIN -> {
                filter
            }

            PersonRole.CANDIDATE -> {
                filter.copy(candidateId = caller.id)
            }

            PersonRole.TEAM_LEAD -> {
                filter.copy(teamleadId = caller.id)
            }

            PersonRole.HR, PersonRole.MANAGER -> {
                val companyId = caller.companyId ?: return null
                filter.copy(companyId = companyId)
            }

            PersonRole.DEVELOPER -> {
                null
            }
        }

    fun checkCanSchedule(
        caller: Person,
        vacancy: VacancyEntity,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        val teamId = caller.teamId
        if (caller.role == PersonRole.TEAM_LEAD && teamId != null && vacancy.team.id == teamId.value) {
            return
        }

        throw AccessDeniedException("Access is denied. You can only schedule interviews for your own team.")
    }

    fun checkCanView(
        caller: Person,
        interview: Interview,
        vacancy: VacancyEntity,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role == PersonRole.CANDIDATE) {
            if (interview.candidateId == caller.id) {
                return
            }
            throw AccessDeniedException("Access is denied. Candidates can view only their own interviews.")
        }

        if (caller.role == PersonRole.TEAM_LEAD) {
            val teamId = caller.teamId
            if (interview.teamleadId == caller.id || (teamId != null && vacancy.team.id == teamId.value)) {
                return
            }
            throw AccessDeniedException("Access is denied. You can only view interviews of your own team.")
        }

        val companyId = caller.companyId
        if ((caller.role == PersonRole.HR || caller.role == PersonRole.MANAGER) &&
            companyId != null &&
            vacancy.team.company.id == companyId.value
        ) {
            return
        }

        throw AccessDeniedException("Access is denied.")
    }

    fun checkCanJoin(
        caller: Person,
        interview: Interview,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role == PersonRole.CANDIDATE && interview.candidateId == caller.id) {
            return
        }

        if (caller.role == PersonRole.TEAM_LEAD && interview.teamleadId == caller.id) {
            return
        }

        throw AccessDeniedException("Access is denied. Only the candidate and the team lead of the interview may connect.")
    }

    fun checkCanUpdateStatus(
        caller: Person,
        interview: Interview,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role == PersonRole.TEAM_LEAD && interview.teamleadId == caller.id) {
            return
        }

        throw AccessDeniedException("Access is denied. Only the team lead who conducts the interview may change its status.")
    }

    fun checkCanViewRecording(
        caller: Person,
        interview: Interview,
        vacancy: VacancyEntity,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        val companyId = caller.companyId
        if (caller.role == PersonRole.HR && companyId != null && vacancy.team.company.id == companyId.value) {
            return
        }

        val teamId = caller.teamId
        if (caller.role == PersonRole.TEAM_LEAD &&
            (interview.teamleadId == caller.id || (teamId != null && vacancy.team.id == teamId.value))
        ) {
            return
        }

        throw AccessDeniedException("Access is denied.")
    }
}
