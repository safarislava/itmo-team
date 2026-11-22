package ru.ifmo.se.api.domain.model

data class ApplicationScore(
    val applicationId: ApplicationId,
    val motivation: Short,
    val softSkills: Short,
    val petProjects: Short,
    val jobExp: Short,
    val hardSkills: Short,
    val workFormat: Short,
)
