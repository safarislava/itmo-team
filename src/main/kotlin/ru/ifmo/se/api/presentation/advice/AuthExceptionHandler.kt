package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class AuthExceptionHandler {
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<ProblemDetail> =
        createProblem(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.message ?: "The email or the password is wrong.")

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(ex: AuthenticationException): ResponseEntity<ProblemDetail> =
        createProblem(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.message ?: "Authentication is required to access this resource.")

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ProblemDetail> =
        createProblem(HttpStatus.FORBIDDEN, "Forbidden", ex.message ?: "Access is denied.")

    private fun createProblem(
        status: HttpStatus,
        title: String,
        detail: String,
    ): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(status, detail).apply {
                type = URI.create("about:blank")
                this.title = title
            }
        return ResponseEntity.status(status).body(problem)
    }
}
