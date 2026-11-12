package ru.ifmo.se.api.infrastructure.postgres.entity

enum class SubmissionVerdict {
    OK,
    WRONG_ANSWER,
    TIME_LIMIT,
    MEMORY_LIMIT,
    RUNTIME_ERROR,
    COMPILATION_ERROR
}
