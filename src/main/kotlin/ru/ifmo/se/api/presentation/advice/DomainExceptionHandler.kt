package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.DomainBadRequestException
import ru.ifmo.se.api.domain.exception.DomainConflictException
import ru.ifmo.se.api.domain.exception.DomainForbiddenException
import ru.ifmo.se.api.domain.exception.DomainNotFoundException
import ru.ifmo.se.api.domain.exception.DomainPayloadTooLargeException
import ru.ifmo.se.api.domain.exception.DomainUnauthorizedException
import java.net.URI

@RestControllerAdvice
class DomainExceptionHandler {
    @ExceptionHandler(DomainNotFoundException::class)
    fun handleNotFound(ex: DomainNotFoundException): ResponseEntity<ProblemDetail> =
        createProblemResponse(HttpStatus.NOT_FOUND, "Not Found", ex.message ?: "Resource not found.")

    @ExceptionHandler(DomainBadRequestException::class)
    fun handleBadRequest(ex: DomainBadRequestException): ResponseEntity<ProblemDetail> =
        createProblemResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.message ?: "Bad request.")

    @ExceptionHandler(DomainConflictException::class)
    fun handleConflict(ex: DomainConflictException): ResponseEntity<ProblemDetail> =
        createProblemResponse(HttpStatus.CONFLICT, "Conflict", ex.message ?: "Conflict.")

    @ExceptionHandler(DomainForbiddenException::class)
    fun handleForbidden(ex: DomainForbiddenException): ResponseEntity<ProblemDetail> =
        createProblemResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.message ?: "Access is denied.")

    @ExceptionHandler(DomainUnauthorizedException::class)
    fun handleUnauthorized(ex: DomainUnauthorizedException): ResponseEntity<ProblemDetail> =
        createProblemResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.message ?: "Unauthorized.")

    @ExceptionHandler(DomainPayloadTooLargeException::class)
    fun handlePayloadTooLarge(ex: DomainPayloadTooLargeException): ResponseEntity<ProblemDetail> =
        createProblemResponse(HttpStatus.CONTENT_TOO_LARGE, "Content Too Large", ex.message ?: "Payload too large.")

    private fun createProblemResponse(
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
