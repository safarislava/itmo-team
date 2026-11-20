package ru.ifmo.se.api.presentation.dto.response

import java.util.UUID

data class LanguageResponse(
    val id: UUID,
    val name: String,
    val version: String,
)
