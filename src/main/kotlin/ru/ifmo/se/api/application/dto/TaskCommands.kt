package ru.ifmo.se.api.application.dto

data class CreateTaskCommand(
    val name: String,
    val description: String?,
    val timeLimit: Int,
    val memoryLimit: Int,
)

data class UpdateTaskCommand(
    val name: String,
    val description: String?,
    val timeLimit: Int,
    val memoryLimit: Int,
)
