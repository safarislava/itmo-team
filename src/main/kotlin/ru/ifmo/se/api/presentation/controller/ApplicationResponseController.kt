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
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.dto.CreateApplicationResponseCommand
import ru.ifmo.se.api.application.dto.UpdateApplicationResponseCommand
import ru.ifmo.se.api.application.service.ApplicationResponseService
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationResponseId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.ScoreMetricsRequest
import ru.ifmo.se.api.presentation.dto.response.ApplicationResponse
import ru.ifmo.se.api.presentation.dto.response.ApplicationResponseResponse
import ru.ifmo.se.api.presentation.mapper.ApplicationResponseMapper
import ru.ifmo.se.api.presentation.mapper.ApplicationResponseResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/applications")
class ApplicationResponseController(
    private val applicationResponseService: ApplicationResponseService,
    private val applicationResponseMapper: ApplicationResponseMapper,
    private val applicationResponseResponseMapper: ApplicationResponseResponseMapper,
) {
    @GetMapping("/next")
    @RequiresRole(PersonRole.HR)
    fun takeNextApplication(
        @AuthenticationPrincipal currentPerson: Person,
    ): ResponseEntity<ApplicationResponse> {
        val details =
            applicationResponseService.takeNextApplication(currentPerson)
                ?: return ResponseEntity.noContent().build()
        return ResponseEntity.ok(applicationResponseMapper.toResponse(details))
    }

    @GetMapping("/{applicationId}/responses")
    @RequiresRole(
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.ADMIN,
    )
    fun listApplicationResponses(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable applicationId: UUID,
    ): ResponseEntity<List<ApplicationResponseResponse>> {
        val responses =
            applicationResponseService.getApplicationResponses(
                currentPerson,
                ApplicationId(applicationId),
            )
        return ResponseEntity.ok(responses.map(applicationResponseResponseMapper::toResponse))
    }

    @PostMapping("/{applicationId}/responses")
    @RequiresRole(PersonRole.HR)
    fun createApplicationResponse(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable applicationId: UUID,
        @Valid @RequestBody request: ScoreMetricsRequest,
    ): ResponseEntity<ApplicationResponseResponse> {
        val command =
            CreateApplicationResponseCommand(
                motivation = request.motivation,
                softSkills = request.softSkills,
                petProjects = request.petProjects,
                jobExp = request.jobExp,
                hardSkills = request.hardSkills,
                workFormat = request.workFormat,
            )
        val created =
            applicationResponseService.createApplicationResponse(
                currentPerson,
                ApplicationId(applicationId),
                command,
            )
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationResponseResponseMapper.toResponse(created))
    }

    @PutMapping("/{applicationId}/responses/{responseId}")
    @RequiresRole(PersonRole.HR)
    fun updateApplicationResponse(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable applicationId: UUID,
        @PathVariable responseId: UUID,
        @Valid @RequestBody request: ScoreMetricsRequest,
    ): ResponseEntity<ApplicationResponseResponse> {
        val command =
            UpdateApplicationResponseCommand(
                motivation = request.motivation,
                softSkills = request.softSkills,
                petProjects = request.petProjects,
                jobExp = request.jobExp,
                hardSkills = request.hardSkills,
                workFormat = request.workFormat,
            )
        val updated =
            applicationResponseService.updateApplicationResponse(
                currentPerson = currentPerson,
                applicationId = ApplicationId(applicationId),
                responseId = ApplicationResponseId(responseId),
                command = command,
            )
        return ResponseEntity.ok(applicationResponseResponseMapper.toResponse(updated))
    }

    @DeleteMapping("/{applicationId}/responses/{responseId}")
    @RequiresRole(PersonRole.HR, PersonRole.ADMIN)
    fun deleteApplicationResponse(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable applicationId: UUID,
        @PathVariable responseId: UUID,
    ): ResponseEntity<Void> {
        applicationResponseService.deleteApplicationResponse(
            currentPerson = currentPerson,
            applicationId = ApplicationId(applicationId),
            responseId = ApplicationResponseId(responseId),
        )
        return ResponseEntity.noContent().build()
    }
}
