package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.Skill
import ru.ifmo.se.api.domain.model.SkillId

interface SkillRepository {
    fun findById(id: SkillId): Skill?

    fun findAll(search: String? = null): List<Skill>

    fun save(skill: Skill): Skill

    fun delete(id: SkillId)
}
