package ru.ifmo.se.api.presentation.dto.request

import ru.ifmo.se.api.domain.model.InterviewStatus

data class InterviewStatusRequest(
    val status: InterviewStatus,
)
