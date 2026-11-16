package ru.ifmo.se.api.application.port.out

interface TokenHasher {
    fun hash(token: String): String
}
