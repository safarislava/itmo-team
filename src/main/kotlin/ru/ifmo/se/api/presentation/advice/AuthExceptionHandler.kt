package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.InvalidCredentialsException
import ru.ifmo.se.api.domain.exception.InvalidRefreshTokenException
import java.net.URI

@RestControllerAdvice
class AuthExceptionHandler {
    @ExceptionHandler(InvalidCredentialsException::class, BadCredentialsException::class)
    fun handleInvalidCredentials(ex: Exception): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.message ?: "The email or the password is wrong.").apply {
                type = URI.create("about:blank")
                title = "Unauthorized"
            }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem)
    }

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleInvalidRefreshToken(ex: InvalidRefreshTokenException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED,
                    ex.message ?: "The refresh token is missing, expired or revoked.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Unauthorized"
                }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(ex: AuthenticationException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED,
                    ex.message ?: "Authentication is required to access this resource.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Unauthorized"
                }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.FORBIDDEN,
                    ex.message ?: "Access is denied.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Forbidden"
                }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem)
    }
}
