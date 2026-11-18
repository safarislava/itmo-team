package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.InterviewFeedbackNotFoundException
import java.net.URI

@RestControllerAdvice
class InterviewFeedbackExceptionHandler {
    @ExceptionHandler(InterviewFeedbackNotFoundException::class)
    fun handleFeedbackNotFound(ex: InterviewFeedbackNotFoundException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    ex.message ?: "The interview does not exist or has no feedback yet.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Not Found"
                }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }
}
