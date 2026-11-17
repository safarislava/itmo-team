package ru.ifmo.se.api.application.port.out

interface EmailService {
    fun sendTemporaryPassword(
        toEmail: String,
        temporaryPassword: String,
    )
}
