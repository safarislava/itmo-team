package ru.ifmo.se.api.presentation.dto.response

import java.util.UUID

data class SkillResponse(
    val id: UUID,
    val name: String,
    val description: String?,
)
