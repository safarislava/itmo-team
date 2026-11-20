package ru.ifmo.se.api.domain.model

data class TaskFilter(
    val search: String? = null,
    val authorId: PersonId? = null,
)
