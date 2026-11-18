package ru.ifmo.se.api.domain.model

data class ApplicationFilter(
    val vacancyId: VacancyId? = null,
    val teamId: TeamId? = null,
    val candidateId: PersonId? = null,
    val companyId: CompanyId? = null,
    val status: ApplicationStatus? = null,
)
