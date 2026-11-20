package ru.ifmo.se.api.application.policy

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole

@Component
class LanguagePolicy {
    fun checkCanManage(caller: Person) {
        if (caller.role != PersonRole.ADMIN) {
            throw AccessDeniedException("Access is denied. Only an administrator may manage programming languages.")
        }
    }
}
