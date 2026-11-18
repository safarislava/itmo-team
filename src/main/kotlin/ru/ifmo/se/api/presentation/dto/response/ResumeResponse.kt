package ru.ifmo.se.api.presentation.dto.response

import java.util.UUID

data class ResumeResponse(
    val id: UUID,
    val filename: String,
)
