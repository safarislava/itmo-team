package ru.ifmo.se.api.infrastructure.mail

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import ru.ifmo.se.api.application.port.out.EmailService
import java.time.Instant
import java.util.UUID

@Service
class SmtpEmailService(
    private val mailSender: JavaMailSender,
) : EmailService {
    override fun sendTemporaryPassword(
        toEmail: String,
        temporaryPassword: String,
    ) {
        val message =
            SimpleMailMessage().apply {
                setTo(toEmail)
                subject = "ITMO're than team - Account Creation"
                text =
                    """
                    Hello,
                    Your account has been created. Your temporary password is: $temporaryPassword
                    Please change your password upon logging in.
                    """.trimIndent()
            }
        mailSender.send(message)
    }

    override fun sendInterviewInvitation(
        toEmail: String,
        interviewId: UUID,
        scheduledStart: Instant,
    ) {
        val message =
            SimpleMailMessage().apply {
                setTo(toEmail)
                subject = "ITMO're than team - Interview Invitation"
                text =
                    """
                    Hello,
                    You have been invited to a live-coding interview scheduled for: $scheduledStart
                    Interview ID: $interviewId
                    """.trimIndent()
            }
        mailSender.send(message)
    }

    override fun sendApplicationRejection(
        toEmail: String,
        vacancyTitle: String,
    ) {
        val message =
            SimpleMailMessage().apply {
                setTo(toEmail)
                subject = "ITMO're than team - Application Update"
                text =
                    """
                    Hello,
                    We regret to inform you that your application for '$vacancyTitle' has been rejected as the vacancy has been closed.
                    """.trimIndent()
            }
        mailSender.send(message)
    }
}
