package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.model.Test
import ru.ifmo.se.api.domain.model.TestId

interface TestRepository {
    fun findById(id: TestId): Test?

    fun findByTaskId(taskId: TaskId): List<Test>

    fun findVisibleByTaskId(taskId: TaskId): List<Test>

    fun save(test: Test): Test

    fun saveAll(tests: List<Test>): List<Test>

    fun delete(id: TestId)

    fun deleteAllByTaskId(taskId: TaskId)
}
