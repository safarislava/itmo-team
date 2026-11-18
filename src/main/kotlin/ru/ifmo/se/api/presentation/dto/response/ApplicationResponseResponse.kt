package ru.ifmo.se.api.presentation.dto.response

import java.util.UUID

data class ApplicationResponseResponse(
    val id: UUID,
    val applicationId: UUID,
    val hr: PersonSummaryResponse,
    val motivation: Short,
    val softSkills: Short,
    val petProjects: Short,
    val jobExp: Short,
    val hardSkills: Short,
    val workFormat: Short,
)
