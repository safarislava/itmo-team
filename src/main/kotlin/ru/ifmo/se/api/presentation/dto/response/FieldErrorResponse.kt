package ru.ifmo.se.api.presentation.dto.response

data class FieldErrorResponse(
    val field: String,
    val message: String,
)
