package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.ApplicationMaxResponsesReachedException
import ru.ifmo.se.api.domain.exception.ApplicationNotInReviewStatusException
import ru.ifmo.se.api.domain.exception.ApplicationResponseAccessDeniedException
import ru.ifmo.se.api.domain.exception.ApplicationResponseNotFoundException
import java.net.URI

@RestControllerAdvice
class ApplicationResponseExceptionHandler {
    @ExceptionHandler(ApplicationResponseNotFoundException::class)
    fun handleApplicationResponseNotFound(ex: ApplicationResponseNotFoundException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    ex.message ?: "Application response not found.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Not Found"
                }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }

    @ExceptionHandler(ApplicationNotInReviewStatusException::class)
    fun handleNotInReview(ex: ApplicationNotInReviewStatusException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    ex.message ?: "Application is not in REVIEW status.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Conflict"
                }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem)
    }

    @ExceptionHandler(ApplicationMaxResponsesReachedException::class)
    fun handleMaxResponses(ex: ApplicationMaxResponsesReachedException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    ex.message ?: "The application has already received the maximum number of responses.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Conflict"
                }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem)
    }

    @ExceptionHandler(ApplicationResponseAccessDeniedException::class)
    fun handleAccessDenied(ex: ApplicationResponseAccessDeniedException): ResponseEntity<ProblemDetail> {
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
