package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.ApplicationResponse
import ru.ifmo.se.api.domain.model.Person

data class ApplicationResponseDetails(
    val response: ApplicationResponse,
    val hr: Person,
)
