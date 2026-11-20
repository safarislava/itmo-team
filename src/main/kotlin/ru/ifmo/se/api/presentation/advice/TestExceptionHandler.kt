package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.TestBadRequestException
import ru.ifmo.se.api.domain.exception.TestNotFoundException
import java.net.URI

@RestControllerAdvice
class TestExceptionHandler {
    @ExceptionHandler(TestNotFoundException::class)
    fun handleTestNotFound(ex: TestNotFoundException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Test not found.").apply {
                type = URI.create("about:blank")
                title = "Not Found"
            }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }

    @ExceptionHandler(TestBadRequestException::class)
    fun handleTestBadRequest(ex: TestBadRequestException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    ex.message ?: "The archive is malformed or a file has no pair.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Bad Request"
                }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
    }
}
