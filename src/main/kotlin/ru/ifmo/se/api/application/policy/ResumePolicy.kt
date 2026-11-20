package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.Resume

@Component
class ResumePolicy {
    fun checkCanAccess(
        caller: Person,
        resume: Resume,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }
        if (resume.candidateId == caller.id) {
            return
        }
        throw AccessDeniedException("Access is denied. Only the owner or admin can access this resume.")
    }

    fun checkCanUpdate(
        caller: Person,
        resume: Resume,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }
        if (resume.candidateId == caller.id) {
            return
        }
        throw AccessDeniedException("Access is denied. Only the owner or admin can update this resume.")
    }
}
