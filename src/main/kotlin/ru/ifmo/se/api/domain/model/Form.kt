package ru.ifmo.se.api.domain.model

import java.util.UUID

data class Form(
    val id: FormId,
    val university: String?,
    val motivation: String?,
    val petProjects: String?,
    val jobExp: String?,
    val workFormat: WorkFormat?,
    val skillIds: List<UUID> = emptyList(),
)
