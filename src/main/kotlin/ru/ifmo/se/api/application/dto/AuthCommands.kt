package ru.ifmo.se.api.application.dto

data class RegisterCommand(
    val name: String,
    val email: String,
    val password: String,
)

data class LoginCommand(
    val email: String,
    val password: String,
)
