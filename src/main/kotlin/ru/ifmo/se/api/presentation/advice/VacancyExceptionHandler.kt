package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.VacancyBadRequestException
import ru.ifmo.se.api.domain.exception.VacancyClosedOrArchivedException
import ru.ifmo.se.api.domain.exception.VacancyInvalidTransitionException
import ru.ifmo.se.api.domain.exception.VacancyNoContestAttachedException
import ru.ifmo.se.api.domain.exception.VacancyNotDraftException
import ru.ifmo.se.api.domain.exception.VacancyNotFoundException
import java.net.URI

@RestControllerAdvice
class VacancyExceptionHandler {
    @ExceptionHandler(VacancyNotFoundException::class)
    fun handleVacancyNotFound(ex: VacancyNotFoundException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Vacancy not found.").apply {
                type = URI.create("about:blank")
                title = "Not Found"
            }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }

    @ExceptionHandler(VacancyBadRequestException::class)
    fun handleVacancyBadRequest(ex: VacancyBadRequestException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Bad request.").apply {
                type = URI.create("about:blank")
                title = "Bad Request"
            }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
    }

    @ExceptionHandler(VacancyNotDraftException::class)
    fun handleVacancyNotDraft(ex: VacancyNotDraftException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message ?: "The vacancy is not a draft.").apply {
                type = URI.create("about:blank")
                title = "Conflict"
            }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem)
    }

    @ExceptionHandler(VacancyClosedOrArchivedException::class)
    fun handleVacancyClosedOrArchived(ex: VacancyClosedOrArchivedException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    ex.message ?: "The vacancy is closed or archived.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Conflict"
                }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem)
    }

    @ExceptionHandler(VacancyInvalidTransitionException::class)
    fun handleVacancyInvalidTransition(ex: VacancyInvalidTransitionException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message ?: "The transition is not allowed.").apply {
                type = URI.create("about:blank")
                title = "Conflict"
            }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem)
    }

    @ExceptionHandler(VacancyNoContestAttachedException::class)
    fun handleVacancyNoContestAttached(ex: VacancyNoContestAttachedException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    ex.message ?: "The vacancy has no contest attached.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Conflict"
                }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem)
    }
}
