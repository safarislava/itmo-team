package ru.ifmo.se.api.presentation.advice

import org.hibernate.exception.ConstraintViolationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class DataIntegrityExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(ex: DataIntegrityViolationException): ResponseEntity<ProblemDetail> {
        val detail = resolveDetail(ex)
        val problem =
            ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, detail).apply {
                type = URI.create("about:blank")
                title = "Conflict"
            }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem)
    }

    private fun resolveDetail(ex: DataIntegrityViolationException): String {
        val context = buildDiagnosticText(ex)
        return CONFLICT_RULES
            .firstOrNull { rule -> rule.matches(context) }
            ?.detail
            ?: DEFAULT_CONFLICT_DETAIL
    }

    private fun buildDiagnosticText(ex: DataIntegrityViolationException): String {
        val constraint = (ex.cause as? ConstraintViolationException)?.constraintName.orEmpty()
        val causeMsg = ex.mostSpecificCause.message.orEmpty()
        val rootMsg = ex.message.orEmpty()
        return "$constraint $causeMsg $rootMsg".lowercase()
    }

    private data class ConflictRule(
        val keywords: List<String>,
        val detail: String,
    ) {
        fun matches(context: String): Boolean = keywords.any { it in context }
    }

    private companion object {
        private const val DEFAULT_CONFLICT_DETAIL =
            "A resource with these unique attributes already exists or is referenced."

        private val CONFLICT_RULES =
            listOf(
                ConflictRule(
                    keywords = listOf("company_name_key", "uq_company_name"),
                    detail = "A company with this name already exists.",
                ),
                ConflictRule(
                    keywords = listOf("email"),
                    detail = "A user with this email already exists.",
                ),
                ConflictRule(
                    keywords = listOf("github"),
                    detail = "The GitHub account is already linked to another user.",
                ),
                ConflictRule(
                    keywords = listOf("не тимлид", "trg_check_team_teamlead", "chk_teamlead", "leads a team"),
                    detail = "Changing the role of a team lead who still leads a team is rejected.",
                ),
                ConflictRule(
                    keywords = listOf("team_teamlead", "fk_person_team"),
                    detail = "The user is referenced by a team as a team lead.",
                ),
                ConflictRule(
                    keywords = listOf("task_author", "author_id"),
                    detail = "The user is referenced by a task as an author.",
                ),
                ConflictRule(
                    keywords = listOf("interview_teamlead"),
                    detail = "The user is referenced by an interview as a team lead.",
                ),
                ConflictRule(
                    keywords = listOf("application_response_hr", "hr_id"),
                    detail = "The user is referenced by an application response as an HR.",
                ),
                ConflictRule(
                    keywords = listOf("violates foreign key constraint", "foreign key", "fkey"),
                    detail = "The user is referenced by a team, a task, an assessment or an interview as a team lead.",
                ),
            )
    }
}
