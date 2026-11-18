package ru.ifmo.se.api.presentation.dto.response

import ru.ifmo.se.api.domain.model.WorkFormat
import java.util.UUID

data class FormResponse(
    val id: UUID,
    val university: String?,
    val motivation: String?,
    val petProjects: String?,
    val jobExp: String?,
    val workFormat: WorkFormat?,
    val skillIds: List<UUID> = emptyList(),
)
