package ru.ifmo.se.api.domain.model

data class Resume(
    val id: ResumeId,
    val candidateId: PersonId,
    val s3key: String,
    val filename: String,
)
