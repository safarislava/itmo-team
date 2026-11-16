package ru.ifmo.se.api.infrastructure.security.annotation

import ru.ifmo.se.api.domain.model.PersonRole

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresRole(
    vararg val value: PersonRole,
)
