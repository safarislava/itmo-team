package ru.ifmo.se.api.application.dto

data class CreateCompanyCommand(
    val name: String,
)

data class UpdateCompanyCommand(
    val name: String,
)
