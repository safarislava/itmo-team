package ru.ifmo.se.api.domain.model

data class Task(
    val id: TaskId,
    val authorId: PersonId,
    val name: String,
    val description: String?,
    val timeLimit: Int,
    val memoryLimit: Int,
)
