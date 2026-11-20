package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.Task

data class TaskDetails(
    val task: Task,
    val author: Person,
)
