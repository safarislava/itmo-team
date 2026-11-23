package ru.ifmo.se.api.domain.exception

class TaskNotFoundException(
    message: String = "Task not found.",
) : DomainNotFoundException(message)

class TaskBadRequestException(
    message: String = "Bad request.",
) : DomainBadRequestException(message)

class TaskNotPartOfContestException(
    message: String = "The task is not a part of the contest.",
) : DomainNotFoundException(message)
