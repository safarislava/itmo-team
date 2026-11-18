package ru.ifmo.se.api.application.dto

import ru.ifmo.se.api.domain.model.WorkFormat
import java.util.UUID

data class CreateFormCommand(
    val university: String? = null,
    val motivation: String? = null,
    val petProjects: String? = null,
    val jobExp: String? = null,
    val workFormat: WorkFormat? = null,
    val skillIds: List<UUID> = emptyList(),
)

data class UpdateFormCommand(
    val university: String? = null,
    val motivation: String? = null,
    val petProjects: String? = null,
    val jobExp: String? = null,
    val workFormat: WorkFormat? = null,
    val skillIds: List<UUID> = emptyList(),
)
