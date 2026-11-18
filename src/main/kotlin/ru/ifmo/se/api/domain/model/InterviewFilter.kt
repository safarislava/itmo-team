package ru.ifmo.se.api.domain.model

import java.time.Instant

data class InterviewFilter(
    val candidateId: PersonId? = null,
    val teamleadId: PersonId? = null,
    val vacancyId: VacancyId? = null,
    val companyId: CompanyId? = null,
    val applicationId: ApplicationId? = null,
    val status: InterviewStatus? = null,
    val dateFrom: Instant? = null,
    val dateTo: Instant? = null,
)
