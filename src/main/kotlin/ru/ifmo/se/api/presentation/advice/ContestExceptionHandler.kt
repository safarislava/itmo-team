package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.ContestAlreadyStartedException
import ru.ifmo.se.api.domain.exception.ContestBadRequestException
import ru.ifmo.se.api.domain.exception.ContestNotFoundException
import java.net.URI

@RestControllerAdvice
class ContestExceptionHandler {
    @ExceptionHandler(ContestNotFoundException::class)
    fun handleContestNotFound(ex: ContestNotFoundException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Contest not found.").apply {
                type = URI.create("about:blank")
                title = "Not Found"
            }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }

    @ExceptionHandler(ContestBadRequestException::class)
    fun handleContestBadRequest(ex: ContestBadRequestException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Bad request.").apply {
                type = URI.create("about:blank")
                title = "Bad Request"
            }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
    }

    @ExceptionHandler(ContestAlreadyStartedException::class)
    fun handleContestAlreadyStarted(ex: ContestAlreadyStartedException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message ?: "The contest has already started.").apply {
                type = URI.create("about:blank")
                title = "Conflict"
            }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem)
    }
}
