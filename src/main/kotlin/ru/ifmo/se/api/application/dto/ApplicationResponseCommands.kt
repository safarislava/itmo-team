package ru.ifmo.se.api.application.dto

data class CreateApplicationResponseCommand(
    val motivation: Short,
    val softSkills: Short,
    val petProjects: Short,
    val jobExp: Short,
    val hardSkills: Short,
    val workFormat: Short,
)

data class UpdateApplicationResponseCommand(
    val motivation: Short,
    val softSkills: Short,
    val petProjects: Short,
    val jobExp: Short,
    val hardSkills: Short,
    val workFormat: Short,
)
