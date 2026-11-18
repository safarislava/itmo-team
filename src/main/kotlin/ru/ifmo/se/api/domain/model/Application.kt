package ru.ifmo.se.api.domain.model

data class Application(
    val id: ApplicationId,
    val candidateId: PersonId,
    val vacancyId: VacancyId,
    val resumeId: ResumeId,
    val formId: FormId,
    val status: ApplicationStatus = ApplicationStatus.NEW,
)
