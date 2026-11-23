package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.annotation.ReadOnlyDataAccess
import ru.ifmo.se.api.application.annotation.WriteDataAccess
import ru.ifmo.se.api.application.dto.CreateTestCommand
import ru.ifmo.se.api.application.dto.ReplaceTestsCommand
import ru.ifmo.se.api.application.dto.TestDetails
import ru.ifmo.se.api.application.dto.UpdateTestCommand
import ru.ifmo.se.api.application.policy.TestPolicy
import ru.ifmo.se.api.application.port.out.TestArchiveExtractor
import ru.ifmo.se.api.application.port.out.TestStorage
import ru.ifmo.se.api.domain.exception.TaskNotFoundException
import ru.ifmo.se.api.domain.exception.TestNotFoundException
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.domain.model.Test
import ru.ifmo.se.api.domain.model.TestId
import ru.ifmo.se.api.domain.repository.ContestRepository
import ru.ifmo.se.api.domain.repository.TaskRepository
import ru.ifmo.se.api.domain.repository.TestRepository
import java.time.Instant

@Service
class TestService(
    private val testRepository: TestRepository,
    private val taskRepository: TaskRepository,
    private val contestRepository: ContestRepository,
    private val testPolicy: TestPolicy,
    private val testStorage: TestStorage,
    private val testArchiveExtractor: TestArchiveExtractor,
) {
    @ReadOnlyDataAccess
    fun listTests(
        caller: Person,
        taskId: TaskId,
    ): List<TestDetails> {
        val task = taskRepository.findById(taskId) ?: throw TaskNotFoundException()
        val isParticipant =
            contestRepository.isCandidateParticipantOfRunningContestWithTask(
                candidateId = caller.id,
                taskId = taskId,
                now = Instant.now(),
            )
        testPolicy.checkCanView(caller, isParticipant)

        val tests =
            if (caller.role == PersonRole.CANDIDATE) {
                testRepository.findVisibleByTaskId(taskId)
            } else {
                testRepository.findByTaskId(taskId)
            }

        return tests.map { test ->
            TestDetails(
                id = test.id,
                taskId = test.taskId,
                input = testStorage.loadInput(test.inputS3key),
                output = testStorage.loadOutput(test.outputS3key),
                hidden = test.isHidden,
            )
        }
    }

    @WriteDataAccess
    @Transactional
    fun createTest(
        caller: Person,
        taskId: TaskId,
        command: CreateTestCommand,
    ): TestDetails {
        val task = taskRepository.findById(taskId) ?: throw TaskNotFoundException()
        testPolicy.checkCanManage(caller, task)

        val testId = TestId.generate()
        val keys = testStorage.save(testId, command.input, command.output)

        val test =
            Test(
                id = testId,
                taskId = taskId,
                inputS3key = keys.inputKey,
                outputS3key = keys.outputKey,
                isHidden = command.hidden,
            )
        testRepository.save(test)

        return TestDetails(
            id = testId,
            taskId = taskId,
            input = command.input,
            output = command.output,
            hidden = command.hidden,
        )
    }

    @WriteDataAccess
    @Transactional
    fun updateTest(
        caller: Person,
        taskId: TaskId,
        testId: TestId,
        command: UpdateTestCommand,
    ): TestDetails {
        val task = taskRepository.findById(taskId) ?: throw TaskNotFoundException()
        testPolicy.checkCanManage(caller, task)

        val test = testRepository.findById(testId) ?: throw TestNotFoundException()
        if (test.taskId != taskId) {
            throw TestNotFoundException()
        }

        val keys = testStorage.save(testId, command.input, command.output)

        val updated =
            test.copy(
                inputS3key = keys.inputKey,
                outputS3key = keys.outputKey,
                isHidden = command.hidden,
            )
        testRepository.save(updated)

        return TestDetails(
            id = testId,
            taskId = taskId,
            input = command.input,
            output = command.output,
            hidden = command.hidden,
        )
    }

    @WriteDataAccess
    @Transactional
    fun deleteTest(
        caller: Person,
        taskId: TaskId,
        testId: TestId,
    ) {
        val task = taskRepository.findById(taskId) ?: throw TaskNotFoundException()
        testPolicy.checkCanManage(caller, task)

        val test = testRepository.findById(testId) ?: throw TestNotFoundException()
        if (test.taskId != taskId) {
            throw TestNotFoundException()
        }

        testStorage.delete(test.inputS3key, test.outputS3key)
        testRepository.delete(testId)
    }

    @WriteDataAccess
    @Transactional
    fun replaceTests(
        caller: Person,
        taskId: TaskId,
        command: ReplaceTestsCommand,
    ): List<TestDetails> {
        val task = taskRepository.findById(taskId) ?: throw TaskNotFoundException()
        testPolicy.checkCanManage(caller, task)

        val archiveTests = testArchiveExtractor.extract(command.bytes)

        val oldTests = testRepository.findByTaskId(taskId)
        oldTests.forEach { oldTest ->
            testStorage.delete(oldTest.inputS3key, oldTest.outputS3key)
        }
        testRepository.deleteAllByTaskId(taskId)

        val savedTests =
            archiveTests.map { (input, output) ->
                val testId = TestId.generate()
                val keys = testStorage.save(testId, input, output)
                val domainTest =
                    Test(
                        id = testId,
                        taskId = taskId,
                        inputS3key = keys.inputKey,
                        outputS3key = keys.outputKey,
                        isHidden = true,
                    )
                val details =
                    TestDetails(
                        id = testId,
                        taskId = taskId,
                        input = input,
                        output = output,
                        hidden = true,
                    )
                domainTest to details
            }

        testRepository.saveAll(savedTests.map { it.first })
        return savedTests.map { it.second }
    }
}
