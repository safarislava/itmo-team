package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.Person

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val person: Person,
)
