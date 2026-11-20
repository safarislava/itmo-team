package ru.ifmo.se.api.domain.exception

class TaskNotFoundException(
    message: String = "Task not found.",
) : RuntimeException(message)

class TaskBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)
