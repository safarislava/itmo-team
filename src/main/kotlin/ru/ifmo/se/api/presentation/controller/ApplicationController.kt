package ru.ifmo.se.api.presentation.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.dto.CreateApplicationCommand
import ru.ifmo.se.api.application.dto.CreateFormCommand
import ru.ifmo.se.api.application.dto.UpdateApplicationStatusCommand
import ru.ifmo.se.api.application.service.ApplicationScoreService
import ru.ifmo.se.api.application.service.ApplicationService
import ru.ifmo.se.api.domain.exception.ApplicationBadRequestException
import ru.ifmo.se.api.domain.model.ApplicationFilter
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.FormId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.ResumeId
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.ApplicationCreateRequest
import ru.ifmo.se.api.presentation.dto.request.ApplicationStatusRequest
import ru.ifmo.se.api.presentation.dto.response.ApplicationResponse
import ru.ifmo.se.api.presentation.dto.response.PageResponse
import ru.ifmo.se.api.presentation.dto.response.ScoreMetricsResponse
import ru.ifmo.se.api.presentation.mapper.ApplicationResponseMapper
import ru.ifmo.se.api.presentation.mapper.ScoreMetricsResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/applications")
class ApplicationController(
    private val applicationService: ApplicationService,
    private val applicationResponseMapper: ApplicationResponseMapper,
    private val applicationScoreService: ApplicationScoreService,
    private val scoreMetricsResponseMapper: ScoreMetricsResponseMapper,
) {
    @PostMapping
    @RequiresRole(PersonRole.CANDIDATE)
    fun createApplication(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: ApplicationCreateRequest,
    ): ResponseEntity<ApplicationResponse> {
        val inlineFormCommand =
            request.form?.let {
                CreateFormCommand(
                    university = it.university,
                    motivation = it.motivation,
                    petProjects = it.petProjects,
                    jobExp = it.jobExp,
                    workFormat = it.workFormat,
                    skillIds = it.skillIds,
                )
            }

        val command =
            CreateApplicationCommand(
                vacancyId = VacancyId(request.vacancyId),
                resumeId = ResumeId(request.resumeId),
                formId = request.formId?.let { FormId(it) },
                inlineForm = inlineFormCommand,
                personalDataConsent = request.personalDataConsent,
            )

        val created = applicationService.createApplication(currentPerson, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationResponseMapper.toResponse(created))
    }

    @GetMapping
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.MANAGER,
        PersonRole.ADMIN,
    )
    fun getApplications(
        @AuthenticationPrincipal currentPerson: Person,
        @Min(0) @RequestParam(defaultValue = "0") page: Int,
        @Min(1) @Max(100) @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") order: String,
        @RequestParam(required = false) vacancyId: UUID?,
        @RequestParam(required = false) teamId: UUID?,
        @RequestParam(required = false) candidateId: UUID?,
        @RequestParam(required = false) status: ApplicationStatus?,
    ): ResponseEntity<PageResponse<ApplicationResponse>> {
        val allowedSorts = setOf("id", "status")
        if (sort !in allowedSorts) {
            throw ApplicationBadRequestException("Invalid sort column: $sort. Allowed: $allowedSorts")
        }

        val sortDirection =
            when (order.lowercase()) {
                "asc" -> SortDirection.ASC
                "desc" -> SortDirection.DESC
                else -> throw ApplicationBadRequestException("Invalid order: $order. Allowed: asc, desc")
            }

        val pageQuery =
            PageQuery(
                pageNumber = page,
                pageSize = size,
                sortBy = sort,
                sortOrder = sortDirection,
            )

        val filter =
            ApplicationFilter(
                vacancyId = vacancyId?.let { VacancyId(it) },
                teamId = teamId?.let { TeamId(it) },
                candidateId = candidateId?.let { PersonId(it) },
                status = status,
            )

        val pageResult =
            applicationService.getApplications(
                currentPerson = currentPerson,
                pageQuery = pageQuery,
                filter = filter,
            )

        val response =
            PageResponse(
                page = pageResult.pageNumber,
                size = size,
                totalElements = pageResult.totalElements,
                totalPages = pageResult.totalPages,
                content = pageResult.items.map(applicationResponseMapper::toResponse),
            )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{applicationId}")
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.MANAGER,
        PersonRole.ADMIN,
    )
    fun getApplication(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable applicationId: UUID,
    ): ResponseEntity<ApplicationResponse> {
        val application = applicationService.getApplication(currentPerson, ApplicationId(applicationId))
        return ResponseEntity.ok(applicationResponseMapper.toResponse(application))
    }

    @PutMapping("/{applicationId}/status")
    @RequiresRole(PersonRole.HR, PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun updateStatus(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable applicationId: UUID,
        @Valid @RequestBody request: ApplicationStatusRequest,
    ): ResponseEntity<Void> {
        val command = UpdateApplicationStatusCommand(request.status)
        applicationService.updateStatus(currentPerson, ApplicationId(applicationId), command)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{applicationId}/score")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.HR, PersonRole.ADMIN)
    fun getApplicationScore(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable applicationId: UUID,
    ): ResponseEntity<ScoreMetricsResponse> {
        val score = applicationScoreService.getScore(currentPerson, ApplicationId(applicationId))
        return ResponseEntity.ok(scoreMetricsResponseMapper.toResponse(score))
    }
}
