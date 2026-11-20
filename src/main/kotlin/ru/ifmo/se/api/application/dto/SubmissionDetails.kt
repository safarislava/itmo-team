package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.Language
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.Submission

data class SubmissionDetails(
    val submission: Submission,
    val sender: Person,
    val language: Language,
)
