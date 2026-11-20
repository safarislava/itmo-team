package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.CreateTaskCommand
import ru.ifmo.se.api.application.dto.TaskDetails
import ru.ifmo.se.api.application.dto.UpdateTaskCommand
import ru.ifmo.se.api.application.policy.TaskPolicy
import ru.ifmo.se.api.application.port.out.FileStorage
import ru.ifmo.se.api.domain.exception.TaskNotFoundException
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.Task
import ru.ifmo.se.api.domain.model.TaskFilter
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.repository.ContestRepository
import ru.ifmo.se.api.domain.repository.PersonRepository
import ru.ifmo.se.api.domain.repository.TaskRepository
import ru.ifmo.se.api.domain.repository.TestRepository
import java.time.Instant

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val testRepository: TestRepository,
    private val contestRepository: ContestRepository,
    private val personRepository: PersonRepository,
    private val taskPolicy: TaskPolicy,
    private val fileStorage: FileStorage,
) {
    fun getTasks(
        caller: Person,
        pageQuery: PageQuery,
        filter: TaskFilter,
    ): PageResult<TaskDetails> {
        val paged = taskRepository.findAll(filter, pageQuery)
        val items =
            paged.items.map { task ->
                val author = personRepository.findById(task.authorId) ?: caller
                TaskDetails(task = task, author = author)
            }
        return PageResult(
            items = items,
            totalElements = paged.totalElements,
            pageNumber = paged.pageNumber,
            totalPages = paged.totalPages,
        )
    }

    fun getTask(
        caller: Person,
        taskId: TaskId,
    ): TaskDetails {
        val task = taskRepository.findById(taskId) ?: throw TaskNotFoundException()
        val isParticipant =
            contestRepository.isCandidateParticipantOfRunningContestWithTask(
                candidateId = caller.id,
                taskId = taskId,
                now = Instant.now(),
            )
        taskPolicy.checkCanView(caller, isParticipant)

        val author = personRepository.findById(task.authorId) ?: caller
        return TaskDetails(task = task, author = author)
    }

    @Transactional
    fun createTask(
        caller: Person,
        command: CreateTaskCommand,
    ): TaskDetails {
        val task =
            Task(
                id = TaskId.generate(),
                authorId = caller.id,
                name = command.name,
                description = command.description,
                timeLimit = command.timeLimit,
                memoryLimit = command.memoryLimit,
            )
        val saved = taskRepository.save(task)
        return TaskDetails(task = saved, author = caller)
    }

    @Transactional
    fun updateTask(
        caller: Person,
        taskId: TaskId,
        command: UpdateTaskCommand,
    ): TaskDetails {
        val task = taskRepository.findById(taskId) ?: throw TaskNotFoundException()
        taskPolicy.checkCanModify(caller, task)

        val updated =
            task.copy(
                name = command.name,
                description = command.description,
                timeLimit = command.timeLimit,
                memoryLimit = command.memoryLimit,
            )
        val saved = taskRepository.save(updated)
        val author = personRepository.findById(saved.authorId) ?: caller
        return TaskDetails(task = saved, author = author)
    }

    @Transactional
    fun deleteTask(
        caller: Person,
        taskId: TaskId,
    ) {
        val task = taskRepository.findById(taskId) ?: throw TaskNotFoundException()
        taskPolicy.checkCanDelete(caller, task)

        val tests = testRepository.findByTaskId(taskId)
        tests.forEach { test ->
            try {
                fileStorage.delete(test.inputS3key)
                fileStorage.delete(test.outputS3key)
            } catch (_: Exception) {
                // best-effort cleanup
            }
        }

        taskRepository.delete(taskId)
    }
}
