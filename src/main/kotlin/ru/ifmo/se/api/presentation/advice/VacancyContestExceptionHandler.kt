package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.VacancyContestAlreadyStartedException
import ru.ifmo.se.api.domain.exception.VacancyContestNotFoundException
import java.net.URI

@RestControllerAdvice
class VacancyContestExceptionHandler {
    @ExceptionHandler(VacancyContestNotFoundException::class)
    fun handleVacancyContestNotFound(ex: VacancyContestNotFoundException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    ex.message ?: "Contest is not attached to this vacancy.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Not Found"
                }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }

    @ExceptionHandler(VacancyContestAlreadyStartedException::class)
    fun handleVacancyContestAlreadyStarted(ex: VacancyContestAlreadyStartedException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    ex.message ?: "The contest has already started.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Conflict"
                }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem)
    }
}
