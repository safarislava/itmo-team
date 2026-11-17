package ru.ifmo.se.api.infrastructure.mail

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import ru.ifmo.se.api.application.port.out.EmailService

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
                subject = "ITMO're than team"
                text =
                    """
                    Hello,
                    Your account has been created. Your temporary password is: $temporaryPassword
                    Please change your password upon logging in.
                    """.trimIndent()
            }
        mailSender.send(message)
    }
}
