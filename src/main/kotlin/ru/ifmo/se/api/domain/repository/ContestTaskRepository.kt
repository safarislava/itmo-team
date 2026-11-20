package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.ContestTask
import ru.ifmo.se.api.domain.model.Task
import ru.ifmo.se.api.domain.model.TaskId

interface ContestTaskRepository {
    fun findById(
        contestId: ContestId,
        taskId: TaskId,
    ): ContestTask?

    fun findByContestId(contestId: ContestId): List<ContestTask>

    fun findTasksByContestId(contestId: ContestId): List<Task>

    fun save(contestTask: ContestTask): ContestTask

    fun delete(
        contestId: ContestId,
        taskId: TaskId,
    )
}
