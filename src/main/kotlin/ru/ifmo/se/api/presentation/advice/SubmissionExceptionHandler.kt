package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.SubmissionAttemptsExhaustedException
import ru.ifmo.se.api.domain.exception.SubmissionBadRequestException
import ru.ifmo.se.api.domain.exception.SubmissionNotFoundException
import ru.ifmo.se.api.domain.exception.SubmissionPayloadTooLargeException
import java.net.URI

@RestControllerAdvice
class SubmissionExceptionHandler {
    @ExceptionHandler(SubmissionNotFoundException::class)
    fun handleSubmissionNotFound(ex: SubmissionNotFoundException): ResponseEntity<ProblemDetail> =
        createProblemResponse(HttpStatus.NOT_FOUND, "Not Found", ex.message ?: "Submission not found.")

    @ExceptionHandler(SubmissionAttemptsExhaustedException::class)
    fun handleSubmissionAttemptsExhausted(ex: SubmissionAttemptsExhaustedException): ResponseEntity<ProblemDetail> =
        createProblemResponse(HttpStatus.CONFLICT, "Conflict", ex.message ?: "The attempts for the task are exhausted.")

    @ExceptionHandler(SubmissionPayloadTooLargeException::class)
    fun handlePayloadTooLarge(ex: SubmissionPayloadTooLargeException): ResponseEntity<ProblemDetail> =
        createProblemResponse(HttpStatus.CONTENT_TOO_LARGE, "Content Too Large", ex.message ?: "Payload too large.")

    @ExceptionHandler(SubmissionBadRequestException::class)
    fun handleBadRequest(ex: SubmissionBadRequestException): ResponseEntity<ProblemDetail> =
        createProblemResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.message ?: "Bad request.")

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
