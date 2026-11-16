package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import ru.ifmo.se.api.presentation.dto.response.FieldErrorResponse
import java.net.URI

@RestControllerAdvice
class ValidationExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val errors =
            ex.bindingResult.fieldErrors.map { fieldError ->
                FieldErrorResponse(
                    field = fieldError.field,
                    message = fieldError.defaultMessage ?: "Invalid value",
                )
            }

        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed").apply {
                type = URI.create("about:blank")
                title = "Bad Request"
                setProperty("errors", errors)
            }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
    }
}
