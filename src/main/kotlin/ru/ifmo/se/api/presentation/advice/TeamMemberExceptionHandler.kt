package ru.ifmo.se.api.presentation.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.ifmo.se.api.domain.exception.InvalidTeamMemberException
import ru.ifmo.se.api.domain.exception.TeamLeadCannotBeRemovedException
import java.net.URI

@RestControllerAdvice
class TeamMemberExceptionHandler {
    @ExceptionHandler(InvalidTeamMemberException::class)
    fun handleInvalidTeamMember(ex: InvalidTeamMemberException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    ex.message ?: "The person is a candidate, belongs to another company or leads another team.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Conflict"
                }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem)
    }

    @ExceptionHandler(TeamLeadCannotBeRemovedException::class)
    fun handleTeamLeadCannotBeRemoved(ex: TeamLeadCannotBeRemovedException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail
                .forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    ex.message ?: "The person is the team lead of the team.",
                ).apply {
                    type = URI.create("about:blank")
                    title = "Conflict"
                }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem)
    }
}
