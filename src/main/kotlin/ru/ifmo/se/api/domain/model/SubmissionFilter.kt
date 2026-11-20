package ru.ifmo.se.api.domain.model

data class SubmissionFilter(
    val contestId: ContestId? = null,
    val taskId: TaskId? = null,
    val senderId: PersonId? = null,
    val status: SubmissionStatus? = null,
    val companyId: CompanyId? = null,
)
