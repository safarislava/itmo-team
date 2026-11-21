package ru.ifmo.se.api.domain.model

data class Skill(
    val id: SkillId,
    val name: String,
    val description: String?,
)
