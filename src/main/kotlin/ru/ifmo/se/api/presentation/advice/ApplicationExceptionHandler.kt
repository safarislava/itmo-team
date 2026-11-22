package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.ApplicationBadRequestException
import ru.ifmo.se.api.domain.exception.ApplicationNotFoundException
import ru.ifmo.se.api.domain.exception.ApplicationScoreExceptions
import java.net.URI

@RestControllerAdvice
class ApplicationExceptionHandler {
    @ExceptionHandler(ApplicationNotFoundException::class)
    fun handleApplicationNotFound(ex: ApplicationNotFoundException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Application not found.").apply {
                type = URI.create("about:blank")
                title = "Not Found"
            }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }

    @ExceptionHandler(ApplicationScoreExceptions::class)
    fun handleApplicationScoreNotFound(ex: ApplicationScoreExceptions): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Application score not found.").apply {
                type = URI.create("about:blank")
                title = "Not Found"
            }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }

    @ExceptionHandler(ApplicationBadRequestException::class)
    fun handleApplicationBadRequest(ex: ApplicationBadRequestException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Bad request.").apply {
                type = URI.create("about:blank")
                title = "Bad Request"
            }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
    }
}
