package ru.ifmo.se.api.application.port.out

interface RefreshTokenGenerator {
    fun generate(): String
}
