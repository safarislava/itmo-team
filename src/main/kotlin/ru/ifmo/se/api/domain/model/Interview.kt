package ru.ifmo.se.api.domain.model

import java.time.Instant

data class Interview(
    val id: InterviewId,
    val candidateId: PersonId,
    val teamleadId: PersonId,
    val vacancyId: VacancyId,
    val videoS3key: String? = null,
    val scheduledStart: Instant,
    val scheduledEnd: Instant,
    val status: InterviewStatus = InterviewStatus.SCHEDULED,
)
