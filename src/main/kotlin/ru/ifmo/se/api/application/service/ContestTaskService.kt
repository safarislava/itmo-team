package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.TaskDetails
import ru.ifmo.se.api.application.policy.ContestTaskPolicy
import ru.ifmo.se.api.domain.exception.ContestAlreadyStartedException
import ru.ifmo.se.api.domain.exception.ContestNotFoundException
import ru.ifmo.se.api.domain.exception.TaskNotFoundException
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.ContestTask
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.repository.ContestRepository
import ru.ifmo.se.api.domain.repository.ContestTaskRepository
import ru.ifmo.se.api.domain.repository.PersonRepository
import ru.ifmo.se.api.domain.repository.TaskRepository
import java.time.Instant

@Service
class ContestTaskService(
    private val contestRepository: ContestRepository,
    private val contestTaskRepository: ContestTaskRepository,
    private val taskRepository: TaskRepository,
    private val personRepository: PersonRepository,
    private val contestTaskPolicy: ContestTaskPolicy,
) {
    fun listContestTasks(
        caller: Person,
        contestId: ContestId,
    ): List<TaskDetails> {
        val contest = contestRepository.findById(contestId) ?: throw ContestNotFoundException()
        val isParticipant = contestRepository.isParticipant(contestId, caller.id)
        contestTaskPolicy.checkCanView(caller, contest, isParticipant)

        val tasks = contestTaskRepository.findTasksByContestId(contestId)
        return tasks.map { task ->
            val author = personRepository.findById(task.authorId) ?: caller
            TaskDetails(task = task, author = author)
        }
    }

    @Transactional
    fun addTaskToContest(
        caller: Person,
        contestId: ContestId,
        taskId: TaskId,
        maxAttempts: Int,
    ) {
        val contest = contestRepository.findById(contestId) ?: throw ContestNotFoundException()
        taskRepository.findById(taskId) ?: throw TaskNotFoundException()
        val companyIds = contestRepository.findCompanyIdsByContestId(contestId)
        contestTaskPolicy.checkCanModify(caller, companyIds)

        val now = Instant.now()
        if (!now.isBefore(contest.scheduledTime)) {
            throw ContestAlreadyStartedException()
        }

        contestTaskRepository.save(
            ContestTask(
                contestId = contestId,
                taskId = taskId,
                maxAttempts = maxAttempts,
            ),
        )
    }

    @Transactional
    fun removeTaskFromContest(
        caller: Person,
        contestId: ContestId,
        taskId: TaskId,
    ) {
        val contest = contestRepository.findById(contestId) ?: throw ContestNotFoundException()
        taskRepository.findById(taskId) ?: throw TaskNotFoundException()
        val companyIds = contestRepository.findCompanyIdsByContestId(contestId)
        contestTaskPolicy.checkCanModify(caller, companyIds)

        val now = Instant.now()
        if (!now.isBefore(contest.scheduledTime)) {
            throw ContestAlreadyStartedException()
        }

        contestTaskRepository.delete(contestId, taskId)
    }
}
