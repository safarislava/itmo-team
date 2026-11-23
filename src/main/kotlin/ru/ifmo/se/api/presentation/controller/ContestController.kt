package ru.ifmo.se.api.presentation.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
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
import ru.ifmo.se.api.application.dto.CreateContestCommand
import ru.ifmo.se.api.application.dto.UpdateContestCommand
import ru.ifmo.se.api.application.service.ContestService
import ru.ifmo.se.api.domain.model.ContestFilter
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.ContestRequest
import ru.ifmo.se.api.presentation.dto.response.ContestResponse
import ru.ifmo.se.api.presentation.dto.response.PageResponse
import ru.ifmo.se.api.presentation.extension.toPageResponse
import ru.ifmo.se.api.presentation.mapper.ContestResponseMapper
import java.time.Instant
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/contests")
class ContestController(
    private val contestService: ContestService,
    private val contestResponseMapper: ContestResponseMapper,
) {
    @GetMapping
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.ADMIN,
    )
    fun listContests(
        @AuthenticationPrincipal currentPerson: Person,
        @Min(0) @RequestParam(defaultValue = "0") page: Int,
        @Min(1) @Max(100) @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(name = "from", required = false) from: Instant?,
        @RequestParam(name = "to", required = false) to: Instant?,
        @RequestParam(required = false) vacancyId: UUID?,
    ): ResponseEntity<PageResponse<ContestResponse>> {
        val pageQuery =
            PageQuery(
                pageNumber = page,
                pageSize = size,
                sortBy = "scheduledTime",
                sortOrder = SortDirection.ASC,
            )

        val filter =
            ContestFilter(
                dateFrom = from,
                dateTo = to,
                vacancyId = vacancyId?.let { VacancyId(it) },
            )

        val pageResult = contestService.getContests(currentPerson, pageQuery, filter)
        return ResponseEntity.ok(pageResult.toPageResponse(size, contestResponseMapper::toResponse))
    }

    @PostMapping
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun createContest(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: ContestRequest,
    ): ResponseEntity<ContestResponse> {
        val command =
            CreateContestCommand(
                name = request.name,
                scheduledTime = request.scheduledTime,
                duration = request.duration,
            )
        val created = contestService.createContest(currentPerson, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(contestResponseMapper.toResponse(created))
    }

    @GetMapping("/{contestId}")
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.ADMIN,
    )
    fun getContest(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable contestId: UUID,
    ): ResponseEntity<ContestResponse> {
        val contest = contestService.getContest(currentPerson, ContestId(contestId))
        return ResponseEntity.ok(contestResponseMapper.toResponse(contest))
    }

    @PutMapping("/{contestId}")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun updateContest(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable contestId: UUID,
        @Valid @RequestBody request: ContestRequest,
    ): ResponseEntity<ContestResponse> {
        val command =
            UpdateContestCommand(
                name = request.name,
                scheduledTime = request.scheduledTime,
                duration = request.duration,
            )
        val updated = contestService.updateContest(currentPerson, ContestId(contestId), command)
        return ResponseEntity.ok(contestResponseMapper.toResponse(updated))
    }

    @DeleteMapping("/{contestId}")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.ADMIN)
    fun deleteContest(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable contestId: UUID,
    ): ResponseEntity<Void> {
        contestService.deleteContest(currentPerson, ContestId(contestId))
        return ResponseEntity.noContent().build()
    }
}
