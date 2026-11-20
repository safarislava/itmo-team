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
        val causeMsg = ex.mostSpecificCause.message.orEmpty()
        if ("собеседование" in causeMsg.lowercase()) {
            return causeMsg.substringAfter("ERROR: ").trim().substringBefore("\n")
        }
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
                    keywords = listOf("uq_application_person_vacancy", "application_person_vacancy"),
                    detail = "The candidate has already applied to this vacancy.",
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
                    keywords = listOf("chk_interview_scheduled_end"),
                    detail = "Scheduled end must be later than scheduled start.",
                ),
                ConflictRule(
                    keywords = listOf("chk_interview_person_teamlead"),
                    detail = "Team lead and candidate cannot be the same person.",
                ),
                ConflictRule(
                    keywords = listOf("interview_feedback_interview_id_key", "uq_interview_feedback"),
                    detail = "Feedback for this interview already exists.",
                ),
                ConflictRule(
                    keywords = listOf("uq_application_response_app_hr", "application_response_app_hr"),
                    detail = "The HR has already assessed this application.",
                ),
                ConflictRule(
                    keywords = listOf("application_response_hr", "hr_id"),
                    detail = "The user is referenced by an application response as an HR.",
                ),
                ConflictRule(
                    keywords = listOf("contest_duration_check", "chk_contest_duration", "contest_duration"),
                    detail = "Contest duration must be positive.",
                ),
                ConflictRule(
                    keywords = listOf("task_time_limit_check", "chk_task_time_limit", "time_limit"),
                    detail = "Time limit must be positive.",
                ),
                ConflictRule(
                    keywords = listOf("task_memory_limit_check", "chk_task_memory_limit", "memory_limit"),
                    detail = "Memory limit must be positive.",
                ),
                ConflictRule(
                    keywords = listOf("task_name_check", "chk_task_name"),
                    detail = "Task name cannot be empty.",
                ),
                ConflictRule(
                    keywords = listOf("contest_task_pkey", "uq_contest_task", "contest_task"),
                    detail = "The task is already part of this contest.",
                ),
                ConflictRule(
                    keywords = listOf("contest_participant_pkey", "uq_contest_participant"),
                    detail = "The candidate is already a participant of this contest.",
                ),
                ConflictRule(
                    keywords = listOf("vacancy_contest_pkey", "uq_vacancy_contest"),
                    detail = "The contest is already associated with this vacancy.",
                ),
                ConflictRule(
                    keywords = listOf("uq_submission_attempt", "submission_attempt"),
                    detail = "The submission attempt already exists.",
                ),
                ConflictRule(
                    keywords = listOf("submission_attempt_check", "chk_submission_attempt"),
                    detail = "Submission attempt must be at least 1.",
                ),
                ConflictRule(
                    keywords = listOf("submission_ai_score_check", "chk_submission_ai_score"),
                    detail = "AI score must be between 0 and 100.",
                ),
                ConflictRule(
                    keywords = listOf("uq_language_name_version", "language_name_version"),
                    detail = "A language with this name and version already exists.",
                ),
                ConflictRule(
                    keywords = listOf("test_task", "test_task_id_fkey"),
                    detail = "The task is referenced by tests.",
                ),
                ConflictRule(
                    keywords = listOf("contest_task_task", "contest_task_task_id_fkey"),
                    detail = "The task is referenced by contests.",
                ),
                ConflictRule(
                    keywords = listOf("contest_task_contest", "contest_task_contest_id_fkey"),
                    detail = "The contest is referenced by tasks.",
                ),
                ConflictRule(
                    keywords = listOf("contest_participant_contest", "contest_participant_contest_id_fkey"),
                    detail = "The contest is referenced by participants.",
                ),
                ConflictRule(
                    keywords = listOf("vacancy_contest_contest", "vacancy_contest_contest_id_fkey"),
                    detail = "The contest is referenced by vacancies.",
                ),
                ConflictRule(
                    keywords = listOf("contest_participant_person", "contest_participant_person_id_fkey"),
                    detail = "The user is referenced by a contest as a participant.",
                ),
                ConflictRule(
                    keywords = listOf("violates foreign key constraint", "foreign key", "fkey"),
                    detail = "The user is referenced by a team, a task, an assessment or an interview as a team lead.",
                ),
            )
    }
}
