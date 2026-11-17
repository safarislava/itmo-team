package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.InvalidCurrentPasswordException
import ru.ifmo.se.api.domain.exception.PersonBadRequestException
import ru.ifmo.se.api.domain.exception.PersonNotFoundException
import java.net.URI

@RestControllerAdvice
class PersonExceptionHandler {
    @ExceptionHandler(PersonNotFoundException::class)
    fun handlePersonNotFound(ex: PersonNotFoundException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Person not found.").apply {
                type = URI.create("about:blank")
                title = "Not Found"
            }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }

    @ExceptionHandler(PersonBadRequestException::class, InvalidCurrentPasswordException::class)
    fun handleBadRequest(ex: RuntimeException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Bad request.").apply {
                type = URI.create("about:blank")
                title = "Bad Request"
            }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
    }
}
