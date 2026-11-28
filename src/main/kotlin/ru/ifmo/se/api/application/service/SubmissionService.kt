package ru.ifmo.se.api.application.service

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.annotation.ReadOnlyDataAccess
import ru.ifmo.se.api.application.annotation.WriteDataAccess
import ru.ifmo.se.api.application.dto.CreateSubmissionCommand
import ru.ifmo.se.api.application.dto.SubmissionDetails
import ru.ifmo.se.api.application.policy.SubmissionPolicy
import ru.ifmo.se.api.application.port.out.SubmissionStorage
import ru.ifmo.se.api.domain.event.SubmissionCreatedEvent
import ru.ifmo.se.api.domain.exception.CandidateNotParticipantException
import ru.ifmo.se.api.domain.exception.ContestNotFoundException
import ru.ifmo.se.api.domain.exception.ContestNotRunningException
import ru.ifmo.se.api.domain.exception.LanguageNotFoundException
import ru.ifmo.se.api.domain.exception.SubmissionAttemptsExhaustedException
import ru.ifmo.se.api.domain.exception.SubmissionNotFoundException
import ru.ifmo.se.api.domain.exception.SubmissionPayloadTooLargeException
import ru.ifmo.se.api.domain.exception.TaskNotFoundException
import ru.ifmo.se.api.domain.exception.TaskNotPartOfContestException
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.Submission
import ru.ifmo.se.api.domain.model.SubmissionFilter
import ru.ifmo.se.api.domain.model.SubmissionId
import ru.ifmo.se.api.domain.model.SubmissionStatus
import ru.ifmo.se.api.domain.model.map
import ru.ifmo.se.api.domain.repository.ContestRepository
import ru.ifmo.se.api.domain.repository.ContestTaskRepository
import ru.ifmo.se.api.domain.repository.LanguageRepository
import ru.ifmo.se.api.domain.repository.PersonRepository
import ru.ifmo.se.api.domain.repository.SubmissionRepository
import ru.ifmo.se.api.domain.repository.TaskRepository
import java.time.Instant

@Service
class SubmissionService(
    private val submissionRepository: SubmissionRepository,
    private val contestRepository: ContestRepository,
    private val contestTaskRepository: ContestTaskRepository,
    private val taskRepository: TaskRepository,
    private val languageRepository: LanguageRepository,
    private val personRepository: PersonRepository,
    private val submissionPolicy: SubmissionPolicy,
    private val submissionStorage: SubmissionStorage,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @ReadOnlyDataAccess
    fun listSubmissions(
        caller: Person,
        pageQuery: PageQuery,
        filter: SubmissionFilter,
    ): PageResult<SubmissionDetails> {
        val scopedFilter =
            submissionPolicy.applyFilterScope(caller, filter)
                ?: return PageResult.empty(pageQuery.pageNumber)

        val page = submissionRepository.findAll(scopedFilter, pageQuery)
        val sendersById = personRepository.findAllById(page.items.map { it.senderId }).associateBy { it.id }
        val languagesById = languageRepository.findAllById(page.items.map { it.languageId }).associateBy { it.id }

        return page.map { submission ->
            val sender = sendersById[submission.senderId] ?: caller
            val language =
                languagesById[submission.languageId]
                    ?: throw LanguageNotFoundException()
            SubmissionDetails(
                submission = submission,
                sender = sender,
                language = language,
            )
        }
    }

    @ReadOnlyDataAccess
    fun getSubmission(
        caller: Person,
        id: SubmissionId,
    ): SubmissionDetails {
        val submission = submissionRepository.findById(id) ?: throw SubmissionNotFoundException()
        val contestCompanyIds = contestRepository.findCompanyIdsByContestId(submission.contestId)
        submissionPolicy.checkCanView(caller, submission, contestCompanyIds)

        val sender =
            personRepository.findById(submission.senderId)
                ?: throw SubmissionNotFoundException()
        val language =
            languageRepository.findById(submission.languageId)
                ?: throw LanguageNotFoundException()

        return SubmissionDetails(
            submission = submission,
            sender = sender,
            language = language,
        )
    }

    @ReadOnlyDataAccess
    fun getSubmissionSource(
        caller: Person,
        id: SubmissionId,
    ): String {
        val submission = submissionRepository.findById(id) ?: throw SubmissionNotFoundException()
        val contestCompanyIds = contestRepository.findCompanyIdsByContestId(submission.contestId)
        submissionPolicy.checkCanView(caller, submission, contestCompanyIds)

        val bytes = submissionStorage.loadSnapshot(submission.snapshotS3key)
        return bytes.toString(Charsets.UTF_8)
    }

    @WriteDataAccess
    @Transactional
    fun createSubmission(
        caller: Person,
        command: CreateSubmissionCommand,
    ): SubmissionDetails {
        submissionPolicy.checkCanCreate(caller)

        val sourceBytes = command.sourceCode.toByteArray(Charsets.UTF_8)
        if (sourceBytes.size > MAX_SOURCE_CODE_SIZE) {
            throw SubmissionPayloadTooLargeException()
        }

        val contest =
            contestRepository.findById(command.contestId)
                ?: throw ContestNotFoundException()

        if (!contestRepository.isParticipant(command.contestId, caller.id)) {
            throw CandidateNotParticipantException()
        }

        taskRepository.findById(command.taskId)
            ?: throw TaskNotFoundException()

        val contestTask =
            contestTaskRepository.findById(command.contestId, command.taskId)
                ?: throw TaskNotPartOfContestException()

        val language =
            languageRepository.findById(command.languageId)
                ?: throw LanguageNotFoundException()

        val now = Instant.now()
        if (now.isBefore(contest.scheduledTime) || now.isAfter(contest.endTime)) {
            throw ContestNotRunningException()
        }

        val attemptsCount = submissionRepository.countAttempts(caller.id, command.contestId, command.taskId)
        if (attemptsCount >= contestTask.maxAttempts) {
            throw SubmissionAttemptsExhaustedException()
        }

        val snapshotKey = submissionStorage.storeSnapshot(sourceBytes)

        val submission =
            Submission(
                id = SubmissionId.generate(),
                senderId = caller.id,
                contestId = command.contestId,
                taskId = command.taskId,
                languageId = command.languageId,
                snapshotS3key = snapshotKey,
                status = SubmissionStatus.QUEUED,
                attempt = attemptsCount + 1,
                aiScore = null,
                submittedAt = now,
                leaseToken = null,
                leaseUntil = null,
                retryCount = 0,
                response = null,
            )

        val saved = submissionRepository.save(submission)

        eventPublisher.publishEvent(
            SubmissionCreatedEvent(
                submissionId = saved.id,
                sourceCode = command.sourceCode,
                languageName = language.name,
            ),
        )

        return SubmissionDetails(
            submission = saved,
            sender = caller,
            language = language,
        )
    }

    private companion object {
        private const val MAX_SOURCE_CODE_SIZE = 2 * 1024 * 1024 // 2 MB
    }
}
