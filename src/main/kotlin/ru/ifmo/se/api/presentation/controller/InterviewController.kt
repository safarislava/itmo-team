package ru.ifmo.se.api.presentation.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpHeaders
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
import ru.ifmo.se.api.application.dto.ScheduleInterviewCommand
import ru.ifmo.se.api.application.dto.UpdateInterviewStatusCommand
import ru.ifmo.se.api.application.service.InterviewService
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.InterviewFilter
import ru.ifmo.se.api.domain.model.InterviewId
import ru.ifmo.se.api.domain.model.InterviewStatus
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.InterviewRequest
import ru.ifmo.se.api.presentation.dto.request.InterviewStatusRequest
import ru.ifmo.se.api.presentation.dto.response.InterviewResponse
import ru.ifmo.se.api.presentation.dto.response.LiveSessionResponse
import ru.ifmo.se.api.presentation.dto.response.PageResponse
import ru.ifmo.se.api.presentation.mapper.InterviewResponseMapper
import java.net.URI
import java.time.Instant
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/interviews")
class InterviewController(
    private val interviewService: InterviewService,
    private val interviewResponseMapper: InterviewResponseMapper,
) {
    @GetMapping
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.MANAGER,
        PersonRole.ADMIN,
    )
    fun listInterviews(
        @AuthenticationPrincipal currentPerson: Person,
        @Min(0) @RequestParam(defaultValue = "0") page: Int,
        @Min(1) @Max(100) @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(name = "from", required = false) from: Instant?,
        @RequestParam(name = "to", required = false) to: Instant?,
        @RequestParam(required = false) status: InterviewStatus?,
        @RequestParam(required = false) applicationId: UUID?,
        @RequestParam(required = false) vacancyId: UUID?,
    ): ResponseEntity<PageResponse<InterviewResponse>> {
        val pageQuery =
            PageQuery(
                pageNumber = page,
                pageSize = size,
                sortBy = "scheduledStart",
                sortOrder = ru.ifmo.se.api.domain.model.SortDirection.ASC,
            )

        val filter =
            InterviewFilter(
                dateFrom = from,
                dateTo = to,
                status = status,
                applicationId = applicationId?.let { ApplicationId(it) },
                vacancyId = vacancyId?.let { VacancyId(it) },
            )

        val pageResult = interviewService.getInterviews(currentPerson, pageQuery, filter)

        val response =
            PageResponse(
                page = pageResult.pageNumber,
                size = size,
                totalElements = pageResult.totalElements,
                totalPages = pageResult.totalPages,
                content = pageResult.items.map(interviewResponseMapper::toResponse),
            )

        return ResponseEntity.ok(response)
    }

    @PostMapping
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun scheduleInterview(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: InterviewRequest,
    ): ResponseEntity<InterviewResponse> {
        val command =
            ScheduleInterviewCommand(
                applicationId = ApplicationId(request.applicationId),
                scheduledStart = request.scheduledStart,
                scheduledEnd = request.scheduledEnd,
            )

        val details = interviewService.scheduleInterview(currentPerson, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(interviewResponseMapper.toResponse(details))
    }

    @GetMapping("/{interviewId}")
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.MANAGER,
        PersonRole.ADMIN,
    )
    fun getInterview(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable interviewId: UUID,
    ): ResponseEntity<InterviewResponse> {
        val details = interviewService.getInterview(currentPerson, InterviewId(interviewId))
        return ResponseEntity.ok(interviewResponseMapper.toResponse(details))
    }

    @PostMapping("/{interviewId}/connections")
    @RequiresRole(PersonRole.CANDIDATE, PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun joinInterview(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable interviewId: UUID,
    ): ResponseEntity<LiveSessionResponse> {
        val session = interviewService.joinInterview(currentPerson, InterviewId(interviewId))
        return ResponseEntity.status(HttpStatus.CREATED).body(interviewResponseMapper.toLiveSessionResponse(session))
    }

    @PutMapping("/{interviewId}/status")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun updateInterviewStatus(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable interviewId: UUID,
        @Valid @RequestBody request: InterviewStatusRequest,
    ): ResponseEntity<InterviewResponse> {
        val command = UpdateInterviewStatusCommand(status = request.status)
        val details = interviewService.updateInterviewStatus(currentPerson, InterviewId(interviewId), command)
        return ResponseEntity.ok(interviewResponseMapper.toResponse(details))
    }

    @GetMapping("/{interviewId}/recording")
    @RequiresRole(PersonRole.HR, PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun getInterviewRecording(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable interviewId: UUID,
    ): ResponseEntity<Void> {
        val url = interviewService.getInterviewRecordingUrl(currentPerson, InterviewId(interviewId))
        val headers =
            HttpHeaders().apply {
                location = URI.create(url)
            }
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build()
    }
}
