package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Form
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole

@Component
class FormPolicy {
    fun checkCanAccess(
        caller: Person,
        form: Form,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }
        if (form.candidateId == caller.id) {
            return
        }
        throw AccessDeniedException("Access is denied. Only the owner or admin can access this form.")
    }

    fun checkCanUpdate(
        caller: Person,
        form: Form,
    ) {
        if (caller.role == PersonRole.ADMIN) {
            return
        }
        if (form.candidateId == caller.id) {
            return
        }
        throw AccessDeniedException("Access is denied. Only the owner or admin can update this form.")
    }
}
