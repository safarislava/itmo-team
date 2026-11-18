package ru.ifmo.se.api.application.port.out

interface EmailService {
    fun sendTemporaryPassword(
        toEmail: String,
        temporaryPassword: String,
    )

    fun sendInterviewInvitation(
        toEmail: String,
        interviewId: java.util.UUID,
        scheduledStart: java.time.Instant,
    )
}
