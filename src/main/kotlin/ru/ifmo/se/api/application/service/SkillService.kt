package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.SkillCommand
import ru.ifmo.se.api.application.policy.SkillPolicy
import ru.ifmo.se.api.domain.exception.SkillNotFoundException
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.Skill
import ru.ifmo.se.api.domain.model.SkillId
import ru.ifmo.se.api.domain.repository.SkillRepository

@Service
class SkillService(
    private val skillRepository: SkillRepository,
    private val skillPolicy: SkillPolicy,
) {
    fun listSkills(search: String?): List<Skill> = skillRepository.findAll(search)

    fun getSkill(id: SkillId): Skill = skillRepository.findById(id) ?: throw SkillNotFoundException()

    @Transactional
    fun createSkill(
        caller: Person,
        command: SkillCommand,
    ): Skill {
        skillPolicy.checkCanManage(caller)
        val skill =
            Skill(
                id = SkillId.generate(),
                name = command.name.trim(),
                description = command.description?.trim()?.takeIf { it.isNotEmpty() },
            )
        return skillRepository.save(skill)
    }

    @Transactional
    fun updateSkill(
        caller: Person,
        id: SkillId,
        command: SkillCommand,
    ): Skill {
        skillPolicy.checkCanManage(caller)
        val existing = skillRepository.findById(id) ?: throw SkillNotFoundException()
        val updated =
            existing.copy(
                name = command.name.trim(),
                description = command.description?.trim()?.takeIf { it.isNotEmpty() },
            )
        return skillRepository.save(updated)
    }

    @Transactional
    fun deleteSkill(
        caller: Person,
        id: SkillId,
    ) {
        skillPolicy.checkCanManage(caller)
        skillRepository.findById(id) ?: throw SkillNotFoundException()
        skillRepository.delete(id)
    }
}
