package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.Size
import ru.ifmo.se.api.domain.model.WorkFormat
import java.util.UUID

data class FormRequest(
    @field:Size(max = 255)
    val university: String? = null,
    val motivation: String? = null,
    val petProjects: String? = null,
    val jobExp: String? = null,
    val workFormat: WorkFormat? = null,
    val skillIds: List<UUID> = emptyList(),
)
