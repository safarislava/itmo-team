package ru.ifmo.se.api.presentation.dto.request

data class TestRequest(
    val input: String,
    val output: String,
    val hidden: Boolean = true,
)
