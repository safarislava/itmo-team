package ru.ifmo.se.api.presentation.dto.request

import ru.ifmo.se.api.domain.model.PersonRole
import java.util.UUID

data class PersonUpdateRequest(
    val role: PersonRole,
    val companyId: UUID? = null,
    val teamId: UUID? = null,
)
