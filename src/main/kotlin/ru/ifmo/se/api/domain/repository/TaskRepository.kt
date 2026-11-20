package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Task
import ru.ifmo.se.api.domain.model.TaskFilter
import ru.ifmo.se.api.domain.model.TaskId

interface TaskRepository {
    fun findById(id: TaskId): Task?

    fun findAll(
        filter: TaskFilter,
        pageQuery: PageQuery,
    ): PageResult<Task>

    fun save(task: Task): Task

    fun delete(id: TaskId)
}
