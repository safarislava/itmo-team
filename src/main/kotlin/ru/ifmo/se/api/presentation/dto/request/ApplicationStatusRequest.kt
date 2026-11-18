package ru.ifmo.se.api.presentation.dto.request

import ru.ifmo.se.api.domain.model.ApplicationStatus

data class ApplicationStatusRequest(
    val status: ApplicationStatus,
)
