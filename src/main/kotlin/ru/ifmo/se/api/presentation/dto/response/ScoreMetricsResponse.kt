package ru.ifmo.se.api.presentation.dto.response

data class ScoreMetricsResponse(
    val motivation: Short,
    val softSkills: Short,
    val petProjects: Short,
    val jobExp: Short,
    val hardSkills: Short,
    val workFormat: Short,
)
