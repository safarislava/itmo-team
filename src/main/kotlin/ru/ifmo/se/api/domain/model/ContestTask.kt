package ru.ifmo.se.api.domain.model

data class ContestTask(
    val contestId: ContestId,
    val taskId: TaskId,
    val maxAttempts: Int,
)
