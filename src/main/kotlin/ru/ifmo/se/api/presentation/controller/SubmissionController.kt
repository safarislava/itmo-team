package ru.ifmo.se.api.presentation.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.dto.CreateSubmissionCommand
import ru.ifmo.se.api.application.service.SubmissionService
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.LanguageId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.domain.model.SubmissionFilter
import ru.ifmo.se.api.domain.model.SubmissionId
import ru.ifmo.se.api.domain.model.SubmissionStatus
import ru.ifmo.se.api.domain.model.TaskId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.SubmissionRequest
import ru.ifmo.se.api.presentation.dto.response.PageResponse
import ru.ifmo.se.api.presentation.dto.response.SubmissionResponse
import ru.ifmo.se.api.presentation.mapper.SubmissionResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/submissions")
class SubmissionController(
    private val submissionService: SubmissionService,
    private val submissionResponseMapper: SubmissionResponseMapper,
) {
    @GetMapping
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.ADMIN,
    )
    fun listSubmissions(
        @AuthenticationPrincipal currentPerson: Person,
        @Min(0) @RequestParam(defaultValue = "0") page: Int,
        @Min(1) @Max(100) @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) contestId: UUID?,
        @RequestParam(required = false) taskId: UUID?,
        @RequestParam(required = false) senderId: UUID?,
        @RequestParam(required = false) status: SubmissionStatus?,
    ): ResponseEntity<PageResponse<SubmissionResponse>> {
        val pageQuery =
            PageQuery(
                pageNumber = page,
                pageSize = size,
                sortBy = "submittedAt",
                sortOrder = SortDirection.DESC,
            )

        val filter =
            SubmissionFilter(
                contestId = contestId?.let(::ContestId),
                taskId = taskId?.let(::TaskId),
                senderId = senderId?.let(::PersonId),
                status = status,
            )

        val pageResult = submissionService.listSubmissions(currentPerson, pageQuery, filter)
        val response =
            PageResponse(
                page = pageResult.pageNumber,
                size = size,
                totalElements = pageResult.totalElements,
                totalPages = pageResult.totalPages,
                content = pageResult.items.map { submissionResponseMapper.toResponse(it, currentPerson) },
            )

        return ResponseEntity.ok(response)
    }

    @PostMapping
    @RequiresRole(PersonRole.CANDIDATE)
    fun createSubmission(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: SubmissionRequest,
    ): ResponseEntity<SubmissionResponse> {
        val command =
            CreateSubmissionCommand(
                contestId = ContestId(request.contestId),
                taskId = TaskId(request.taskId),
                languageId = LanguageId(request.languageId),
                sourceCode = request.sourceCode,
            )
        val created = submissionService.createSubmission(currentPerson, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            submissionResponseMapper.toResponse(created, currentPerson),
        )
    }

    @GetMapping("/{submissionId}")
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.ADMIN,
    )
    fun getSubmission(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable submissionId: UUID,
    ): ResponseEntity<SubmissionResponse> {
        val submission = submissionService.getSubmission(currentPerson, SubmissionId(submissionId))
        return ResponseEntity.ok(submissionResponseMapper.toResponse(submission, currentPerson))
    }

    @GetMapping("/{submissionId}/source", produces = [MediaType.TEXT_PLAIN_VALUE])
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.ADMIN,
    )
    fun getSubmissionSource(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable submissionId: UUID,
    ): ResponseEntity<String> {
        val sourceCode = submissionService.getSubmissionSource(currentPerson, SubmissionId(submissionId))
        return ResponseEntity.ok(sourceCode)
    }
}
