package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.dto.CreatePersonCommand
import ru.ifmo.se.api.application.dto.UpdatePersonCommand
import ru.ifmo.se.api.domain.exception.PersonBadRequestException
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonFilter
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonRole

@Component
class PersonPolicy {
    fun applyFilterScope(
        caller: Person,
        filter: PersonFilter,
    ): PersonFilter? {
        val callerCompanyId = caller.companyId
        val callerTeamId = caller.teamId

        return when (caller.role) {
            PersonRole.ADMIN -> {
                filter
            }

            PersonRole.HR, PersonRole.MANAGER -> {
                val companyId = callerCompanyId ?: return null
                filter.copy(companyId = companyId)
            }

            PersonRole.TEAM_LEAD -> {
                if (callerCompanyId == null || callerTeamId == null) return null
                filter.copy(companyId = callerCompanyId, teamId = callerTeamId)
            }

            PersonRole.DEVELOPER, PersonRole.CANDIDATE -> {
                null
            }
        }
    }

    fun checkCanView(
        caller: Person,
        target: Person,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }

        if (caller.role == PersonRole.HR || caller.role == PersonRole.TEAM_LEAD || caller.role == PersonRole.MANAGER) {
            val callerCompanyId = caller.companyId
            if (callerCompanyId != null && target.companyId == callerCompanyId) {
                return
            }
            throw AccessDeniedException("Access is denied. User does not have access to view this person.")
        }

        throw AccessDeniedException("Access is denied.")
    }

    fun checkCanCreate(
        caller: Person,
        command: CreatePersonCommand,
    ) {
        when (caller.role) {
            PersonRole.ADMIN -> {
            }

            PersonRole.HR -> {
                if (command.role == PersonRole.ADMIN) {
                    throw AccessDeniedException("HR cannot assign ADMIN role.")
                }
            }

            else -> {
                throw AccessDeniedException("Access is denied.")
            }
        }
    }

    fun checkCanUpdate(
        caller: Person,
        target: Person,
        command: UpdatePersonCommand,
    ) {
        when (caller.role) {
            PersonRole.ADMIN -> {
            }

            PersonRole.HR -> {
                val hrCompanyId = caller.companyId ?: throw PersonBadRequestException("HR has no company assigned.")
                if (target.companyId != null && target.companyId != hrCompanyId) {
                    throw AccessDeniedException("Access is denied. Cannot edit users outside of your company.")
                }
                if (target.role == PersonRole.ADMIN) {
                    throw AccessDeniedException("Access is denied. HR cannot edit administrators.")
                }
                if (command.role == PersonRole.ADMIN) {
                    throw AccessDeniedException("Access is denied. HR cannot assign ADMIN role.")
                }
            }

            else -> {
                throw AccessDeniedException("Access is denied.")
            }
        }
    }

    fun checkCanDelete(
        caller: Person,
        targetPersonId: PersonId,
    ) {
        if (caller.id != targetPersonId && caller.role != PersonRole.ADMIN) {
            throw AccessDeniedException("Access is denied. Only administrators can delete other users.")
        }
    }

    fun checkCanDismiss(
        caller: Person,
        target: Person,
    ) {
        if (target.role == PersonRole.ADMIN) {
            throw AccessDeniedException("Access is denied. Administrators cannot be dismissed.")
        }

        when (caller.role) {
            PersonRole.ADMIN -> {
            }

            PersonRole.HR -> {
                val hrCompanyId = caller.companyId ?: throw PersonBadRequestException("HR has no company assigned.")
                if (target.companyId != hrCompanyId) {
                    throw AccessDeniedException("Access is denied. Cannot dismiss users outside of your company.")
                }
            }

            else -> {
                throw AccessDeniedException("Access is denied.")
            }
        }
    }
}
