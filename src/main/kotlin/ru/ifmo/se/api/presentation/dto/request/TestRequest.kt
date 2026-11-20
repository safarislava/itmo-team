package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.NotNull

data class TestRequest(
    @field:NotNull
    val input: String,
    @field:NotNull
    val output: String,
    val hidden: Boolean = true,
)
