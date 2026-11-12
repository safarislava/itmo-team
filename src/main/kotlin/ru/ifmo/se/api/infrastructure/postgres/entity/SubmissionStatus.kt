package ru.ifmo.se.api.infrastructure.postgres.entity

enum class SubmissionStatus {
    QUEUED,
    RUNNING,
    FINISHED,
    FAILED,
}
