package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.exception.VacancyClosedOrArchivedException
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Team
import ru.ifmo.se.api.domain.model.Vacancy
import ru.ifmo.se.api.domain.model.VacancyStatus

@Component
class VacancyContestPolicy {
    fun checkCanListContests(
        caller: Person?,
        team: Team,
        isCandidateApplicant: Boolean,
    ) {
        if (caller == null) {
            throw AccessDeniedException("Full authentication is required to access this resource.")
        }

        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role == PersonRole.CANDIDATE) {
            if (!isCandidateApplicant) {
                throw AccessDeniedException("A candidate sees the contests only after applying to the vacancy.")
            }
            return
        }

        val internalRoles = setOf(PersonRole.HR, PersonRole.TEAM_LEAD, PersonRole.DEVELOPER, PersonRole.MANAGER)
        if (caller.role in internalRoles && caller.companyId == team.companyId) {
            return
        }

        throw AccessDeniedException("Access is denied.")
    }

    fun checkCanAttachContest(
        caller: Person,
        vacancy: Vacancy,
        team: Team,
    ) {
        if (vacancy.status.isClosedOrArchived) {
            throw VacancyClosedOrArchivedException()
        }

        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role != PersonRole.TEAM_LEAD || caller.id != team.teamleadId || caller.teamId != team.id) {
            throw AccessDeniedException("Only a team lead of the team of the vacancy may attach contests.")
        }
    }

    fun checkCanDetachContest(
        caller: Person,
        team: Team,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role != PersonRole.TEAM_LEAD || caller.id != team.teamleadId || caller.teamId != team.id) {
            throw AccessDeniedException("Only a team lead of the team of the vacancy may detach contests.")
        }
    }

    private val VacancyStatus.isClosedOrArchived: Boolean
        get() = this == VacancyStatus.CLOSED || this == VacancyStatus.ARCHIVE
}
