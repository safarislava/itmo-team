package ru.ifmo.se.api.domain.model

import java.time.Instant

data class ContestFilter(
    val dateFrom: Instant? = null,
    val dateTo: Instant? = null,
    val vacancyId: VacancyId? = null,
    val participantId: PersonId? = null,
    val companyId: CompanyId? = null,
)
