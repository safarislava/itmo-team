package ru.ifmo.se.api.presentation.dto.response

import java.util.UUID

data class TaskResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val timeLimit: Int,
    val memoryLimit: Int,
    val author: PersonSummaryResponse,
)
