package ru.ifmo.se.api.application.port.out

import ru.ifmo.se.api.domain.model.Person

interface AccessTokenGenerator {
    fun generate(person: Person): String
}
