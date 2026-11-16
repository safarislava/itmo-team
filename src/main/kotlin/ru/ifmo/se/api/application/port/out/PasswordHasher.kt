package ru.ifmo.se.api.application.port.out

interface PasswordHasher {
    fun hash(rawPassword: String): String

    fun matches(
        rawPassword: String,
        hashedPassword: String,
    ): Boolean
}
