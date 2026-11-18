package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.PersonRole
import java.util.UUID

data class PersonSummaryResponse(
    val id: UUID,
    val name: String,
    val role: PersonRole,
)
