package ru.ifmo.se.api.presentation.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.dto.SkillCommand
import ru.ifmo.se.api.application.service.SkillService
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.SkillId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.SkillRequest
import ru.ifmo.se.api.presentation.dto.response.SkillResponse
import ru.ifmo.se.api.presentation.mapper.SkillResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/skills")
class SkillController(
    private val skillService: SkillService,
    private val skillResponseMapper: SkillResponseMapper,
) {
    @GetMapping
    fun listSkills(
        @RequestParam(required = false) search: String?,
    ): ResponseEntity<List<SkillResponse>> {
        val skills = skillService.listSkills(search)
        return ResponseEntity.ok(skills.map(skillResponseMapper::toResponse))
    }

    @GetMapping("/{skillId}")
    fun getSkill(
        @PathVariable skillId: UUID,
    ): ResponseEntity<SkillResponse> {
        val skill = skillService.getSkill(SkillId(skillId))
        return ResponseEntity.ok(skillResponseMapper.toResponse(skill))
    }

    @PostMapping
    @RequiresRole(PersonRole.ADMIN)
    fun createSkill(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: SkillRequest,
    ): ResponseEntity<SkillResponse> {
        val command = SkillCommand(name = request.name, description = request.description)
        val created = skillService.createSkill(currentPerson, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(skillResponseMapper.toResponse(created))
    }

    @PutMapping("/{skillId}")
    @RequiresRole(PersonRole.ADMIN)
    fun updateSkill(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable skillId: UUID,
        @Valid @RequestBody request: SkillRequest,
    ): ResponseEntity<SkillResponse> {
        val command = SkillCommand(name = request.name, description = request.description)
        val updated = skillService.updateSkill(currentPerson, SkillId(skillId), command)
        return ResponseEntity.ok(skillResponseMapper.toResponse(updated))
    }

    @DeleteMapping("/{skillId}")
    @RequiresRole(PersonRole.ADMIN)
    fun deleteSkill(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable skillId: UUID,
    ): ResponseEntity<Void> {
        skillService.deleteSkill(currentPerson, SkillId(skillId))
        return ResponseEntity.noContent().build()
    }
}
