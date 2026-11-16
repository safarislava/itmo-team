package ru.ifmo.se.api.application.port.out

interface RefreshTokenPolicy {
    val expirationSeconds: Long
}
