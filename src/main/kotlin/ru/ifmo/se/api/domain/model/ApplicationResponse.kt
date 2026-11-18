package ru.ifmo.se.api.domain.model

data class ApplicationResponse(
    val id: ApplicationResponseId,
    val applicationId: ApplicationId,
    val hrId: PersonId,
    val motivation: Short,
    val softSkills: Short,
    val petProjects: Short,
    val jobExp: Short,
    val hardSkills: Short,
    val workFormat: Short,
)
