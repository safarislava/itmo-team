package ru.ifmo.se.api.application.port.out

interface AccessTokenPolicy {
    val expirationSeconds: Long
}
