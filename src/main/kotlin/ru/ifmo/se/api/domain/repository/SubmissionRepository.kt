package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.Submission
import ru.ifmo.se.api.domain.model.SubmissionFilter
import ru.ifmo.se.api.domain.model.SubmissionId
import ru.ifmo.se.api.domain.model.TaskId

interface SubmissionRepository {
    fun findById(id: SubmissionId): Submission?

    fun findAll(
        filter: SubmissionFilter,
        pageQuery: PageQuery,
    ): PageResult<Submission>

    fun save(submission: Submission): Submission

    fun updateAiScore(
        id: SubmissionId,
        aiScore: Short,
    )

    fun countAttempts(
        senderId: PersonId,
        contestId: ContestId,
        taskId: TaskId,
    ): Int
}
