package ru.ifmo.se.api.domain.exception

class TaskNotFoundException(
    message: String = "Task not found.",
) : RuntimeException(message)

class TaskBadRequestException(
    message: String = "Bad request.",
) : RuntimeException(message)

class TaskNotPartOfContestException(
    message: String = "The task is not a part of the contest.",
) : RuntimeException(message)
