package ru.ifmo.se.api.domain.model

data class LeaderboardEntry(
    val applicationId: ApplicationId,
    val candidateId: PersonId,
    val candidateName: String,
    val vacancyId: VacancyId,
    val resumeId: ResumeId,
    val formId: FormId,
    val status: ApplicationStatus,
    val score: Double,
    val scoreMetrics: ApplicationScore? = null,
)
