package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.InterviewBadRequestException
import ru.ifmo.se.api.domain.exception.InterviewNotFoundException
import ru.ifmo.se.api.domain.exception.InterviewRecordingNotFoundException
import java.net.URI

@RestControllerAdvice
class InterviewExceptionHandler {
    @ExceptionHandler(InterviewNotFoundException::class)
    fun handleInterviewNotFound(ex: InterviewNotFoundException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    ex.message ?: "Interview not found.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Not Found"
                }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }

    @ExceptionHandler(InterviewRecordingNotFoundException::class)
    fun handleRecordingNotFound(ex: InterviewRecordingNotFoundException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    ex.message ?: "The interview does not exist or its recording is not available.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Not Found"
                }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }

    @ExceptionHandler(InterviewBadRequestException::class)
    fun handleBadRequest(ex: InterviewBadRequestException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    ex.message ?: "Bad request.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Bad Request"
                }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
    }
}
