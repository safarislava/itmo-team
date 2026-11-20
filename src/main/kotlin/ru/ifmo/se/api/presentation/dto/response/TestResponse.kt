package ru.ifmo.se.api.presentation.dto.response

import java.util.UUID

data class TestResponse(
    val id: UUID,
    val taskId: UUID,
    val input: String,
    val output: String,
    val hidden: Boolean,
)
