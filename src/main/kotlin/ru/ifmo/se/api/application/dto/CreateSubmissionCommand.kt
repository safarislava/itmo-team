package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.LanguageId
import ru.ifmo.se.api.domain.model.TaskId

data class CreateSubmissionCommand(
    val contestId: ContestId,
    val taskId: TaskId,
    val languageId: LanguageId,
    val sourceCode: String,
)
