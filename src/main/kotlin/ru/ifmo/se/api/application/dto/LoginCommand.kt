package ru.ifmo.se.api.application.dto

data class LoginCommand(
    val email: String,
    val password: String,
)
