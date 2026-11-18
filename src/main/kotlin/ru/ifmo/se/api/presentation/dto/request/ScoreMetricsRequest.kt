package ru.ifmo.se.api.presentation.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class ScoreMetricsRequest(
    @field:Min(0)
    @field:Max(100)
    val motivation: Short,
    @field:Min(0)
    @field:Max(100)
    val softSkills: Short,
    @field:Min(0)
    @field:Max(100)
    val petProjects: Short,
    @field:Min(0)
    @field:Max(100)
    val jobExp: Short,
    @field:Min(0)
    @field:Max(100)
    val hardSkills: Short,
    @field:Min(0)
    @field:Max(100)
    val workFormat: Short,
)
