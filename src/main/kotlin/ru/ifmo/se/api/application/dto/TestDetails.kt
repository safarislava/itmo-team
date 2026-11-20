package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.model.TestId

data class TestDetails(
    val id: TestId,
    val taskId: TaskId,
    val input: String,
    val output: String,
    val hidden: Boolean,
)
