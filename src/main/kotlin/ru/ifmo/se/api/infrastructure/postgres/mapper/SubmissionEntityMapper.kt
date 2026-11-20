package ru.ifmo.se.api.infrastructure.postgres.mapper

import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.LanguageId
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.Submission
import ru.ifmo.se.api.domain.model.SubmissionId
import ru.ifmo.se.api.domain.model.SubmissionResult
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.infrastructure.postgres.entity.SubmissionEntity
import ru.ifmo.se.api.infrastructure.postgres.entity.SubmissionResponseEntity
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaContestRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaLanguageRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaPersonRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaTaskRepository

@Component
class SubmissionEntityMapper(
    private val personRepository: JpaPersonRepository,
    private val contestRepository: JpaContestRepository,
    private val taskRepository: JpaTaskRepository,
    private val languageRepository: JpaLanguageRepository,
) {
    fun toDomain(
        entity: SubmissionEntity,
        responseEntity: SubmissionResponseEntity? = null,
    ): Submission {
        val result =
            responseEntity?.let {
                SubmissionResult(
                    verdict = it.verdict,
                    executionTime = it.executionTime,
                    memoryUsed = it.memoryUsed,
                )
            }
        return Submission(
            id = SubmissionId(entity.id),
            senderId = PersonId(entity.sender.id),
            contestId = ContestId(entity.contest.id),
            taskId = TaskId(entity.task.id),
            languageId = LanguageId(entity.language.id),
            snapshotS3key = entity.snapshotS3key,
            status = entity.status,
            attempt = entity.attempt.toInt(),
            aiScore = entity.aiScore?.toInt(),
            submittedAt = entity.submittedAt,
            result = result,
        )
    }

    fun toEntity(domain: Submission): SubmissionEntity =
        SubmissionEntity(
            id = domain.id.value,
            sender = personRepository.getReferenceById(domain.senderId.value),
            contest = contestRepository.getReferenceById(domain.contestId.value),
            task = taskRepository.getReferenceById(domain.taskId.value),
            language = languageRepository.getReferenceById(domain.languageId.value),
            snapshotS3key = domain.snapshotS3key,
            status = domain.status,
            attempt = domain.attempt.toShort(),
            aiScore = domain.aiScore?.toShort(),
            submittedAt = domain.submittedAt,
        )
}
