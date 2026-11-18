package ru.ifmo.se.api.presentation.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.dto.CreateFormCommand
import ru.ifmo.se.api.application.dto.UpdateFormCommand
import ru.ifmo.se.api.application.service.FormService
import ru.ifmo.se.api.domain.model.FormId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.FormRequest
import ru.ifmo.se.api.presentation.dto.response.FormResponse
import ru.ifmo.se.api.presentation.mapper.FormResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/forms")
class FormController(
    private val formService: FormService,
    private val formResponseMapper: FormResponseMapper,
) {
    @PostMapping
    @RequiresRole(PersonRole.CANDIDATE, PersonRole.ADMIN)
    fun createForm(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: FormRequest,
    ): ResponseEntity<FormResponse> {
        val command =
            CreateFormCommand(
                university = request.university,
                motivation = request.motivation,
                petProjects = request.petProjects,
                jobExp = request.jobExp,
                workFormat = request.workFormat,
                skillIds = request.skillIds,
            )
        val created = formService.createForm(currentPerson, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(formResponseMapper.toResponse(created))
    }

    @GetMapping("/{formId}")
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.ADMIN,
    )
    fun getForm(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable formId: UUID,
    ): ResponseEntity<FormResponse> {
        val form = formService.getForm(currentPerson, FormId(formId))
        return ResponseEntity.ok(formResponseMapper.toResponse(form))
    }

    @PutMapping("/{formId}")
    @RequiresRole(PersonRole.CANDIDATE, PersonRole.ADMIN)
    fun updateForm(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable formId: UUID,
        @Valid @RequestBody request: FormRequest,
    ): ResponseEntity<FormResponse> {
        val command =
            UpdateFormCommand(
                university = request.university,
                motivation = request.motivation,
                petProjects = request.petProjects,
                jobExp = request.jobExp,
                workFormat = request.workFormat,
                skillIds = request.skillIds,
            )
        val updated = formService.updateForm(currentPerson, FormId(formId), command)
        return ResponseEntity.ok(formResponseMapper.toResponse(updated))
    }
}
