package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.ApplicationStatus

data class HiringApplicationsResponse(
    val total: Int,
    val byStatus: Map<ApplicationStatus, Int>,
)
