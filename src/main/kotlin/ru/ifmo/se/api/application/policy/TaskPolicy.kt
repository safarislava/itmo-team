package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Task

@Component
class TaskPolicy {
    fun checkCanView(
        caller: Person,
        isParticipantOfRunningContest: Boolean,
    ) {
        if (caller.role == PersonRole.ADMIN || caller.role == PersonRole.TEAM_LEAD) return

        if (caller.role == PersonRole.CANDIDATE) {
            if (!isParticipantOfRunningContest) {
                throw AccessDeniedException("Access is denied. Candidates may only view tasks of running contests they participate in.")
            }
            return
        }

        throw AccessDeniedException("Access is denied.")
    }

    fun checkCanModify(
        caller: Person,
        task: Task,
    ) {
        if (caller.role == PersonRole.ADMIN) return

        if (caller.role == PersonRole.TEAM_LEAD) {
            if (task.authorId != caller.id) {
                throw AccessDeniedException("Access is denied. A team lead may modify only the tasks they authored.")
            }
            return
        }

        throw AccessDeniedException("Access is denied.")
    }

    fun checkCanDelete(
        caller: Person,
        task: Task,
    ) {
        if (caller.role == PersonRole.ADMIN) return

        if (caller.role == PersonRole.TEAM_LEAD) {
            if (task.authorId != caller.id) {
                throw AccessDeniedException("Access is denied. A team lead may delete only the tasks they authored.")
            }
            return
        }

        throw AccessDeniedException("Access is denied.")
    }
}
