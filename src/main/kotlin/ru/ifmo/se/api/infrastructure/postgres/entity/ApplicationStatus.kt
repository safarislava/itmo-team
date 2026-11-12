package ru.ifmo.se.api.infrastructure.postgres.entity

enum class ApplicationStatus {
    NEW,
    REVIEW,
    CONTEST,
    INTERVIEW,
    OFFER,
    REJECTED
}
