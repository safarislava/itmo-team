package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.ResumeBadRequestException
import ru.ifmo.se.api.domain.exception.ResumeLockedException
import ru.ifmo.se.api.domain.exception.ResumeNotFoundException
import java.net.URI

@RestControllerAdvice
class ResumeExceptionHandler {
    @ExceptionHandler(ResumeNotFoundException::class)
    fun handleResumeNotFound(ex: ResumeNotFoundException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Resume not found.").apply {
                type = URI.create("about:blank")
                title = "Not Found"
            }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }

    @ExceptionHandler(ResumeBadRequestException::class)
    fun handleResumeBadRequest(ex: ResumeBadRequestException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Bad request.").apply {
                type = URI.create("about:blank")
                title = "Bad Request"
            }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
    }

    @ExceptionHandler(ResumeLockedException::class)
    fun handleResumeLocked(ex: ResumeLockedException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message ?: "Resume is locked.").apply {
                type = URI.create("about:blank")
                title = "Conflict"
            }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem)
    }
}
