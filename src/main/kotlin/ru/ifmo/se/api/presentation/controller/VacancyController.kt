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
import ru.ifmo.se.api.application.dto.CreateVacancyCommand
import ru.ifmo.se.api.application.dto.UpdateVacancyCommand
import ru.ifmo.se.api.application.dto.UpdateVacancyStatusCommand
import ru.ifmo.se.api.application.service.ApplicationScoreService
import ru.ifmo.se.api.application.service.VacancyService
import ru.ifmo.se.api.domain.exception.VacancyBadRequestException
import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.LeaderboardFilter
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.SkillId
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.model.VacancyFilter
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.domain.model.VacancyStatus
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.VacancyRequest
import ru.ifmo.se.api.presentation.dto.request.VacancyStatusRequest
import ru.ifmo.se.api.presentation.dto.response.LeaderboardEntryResponse
import ru.ifmo.se.api.presentation.dto.response.PageResponse
import ru.ifmo.se.api.presentation.dto.response.VacancyResponse
import ru.ifmo.se.api.presentation.dto.response.VacancyStatusResponse
import ru.ifmo.se.api.presentation.mapper.LeaderboardResponseMapper
import ru.ifmo.se.api.presentation.mapper.VacancyResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/vacancies")
class VacancyController(
    private val vacancyService: VacancyService,
    private val vacancyResponseMapper: VacancyResponseMapper,
    private val applicationScoreService: ApplicationScoreService,
    private val leaderboardResponseMapper: LeaderboardResponseMapper,
) {
    @GetMapping
    fun listVacancies(
        @AuthenticationPrincipal currentPerson: Person?,
        @Min(0) @RequestParam(defaultValue = "0") page: Int,
        @Min(1) @Max(100) @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "createdAt") sort: String,
        @RequestParam(defaultValue = "desc") order: String,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) skillIds: List<UUID>?,
        @RequestParam(required = false) city: String?,
        @Min(0) @RequestParam(required = false) minSalary: Int?,
        @RequestParam(required = false) status: VacancyStatus?,
        @RequestParam(required = false) companyId: UUID?,
        @RequestParam(required = false) teamId: UUID?,
    ): ResponseEntity<PageResponse<VacancyResponse>> {
        val allowedSorts = setOf("id", "title", "salary", "createdAt")
        if (sort !in allowedSorts) {
            throw VacancyBadRequestException("Invalid sort field '$sort'. Allowed values: ${allowedSorts.joinToString()}")
        }

        val sortDirection =
            when (order.lowercase()) {
                "asc" -> SortDirection.ASC
                "desc" -> SortDirection.DESC
                else -> throw VacancyBadRequestException("Invalid order '$order'. Allowed values: asc, desc.")
            }

        val filter =
            VacancyFilter(
                search = search,
                skillIds = skillIds?.map { SkillId(it) }?.toSet(),
                city = city,
                minSalary = minSalary,
                status = status,
                companyId = companyId?.let { CompanyId(it) },
                teamId = teamId?.let { TeamId(it) },
            )

        val pageResult = vacancyService.listVacancies(currentPerson, filter, PageQuery(page, size, sort, sortDirection))

        return ResponseEntity.ok(
            PageResponse(
                page = pageResult.pageNumber,
                size = size,
                totalElements = pageResult.totalElements,
                totalPages = pageResult.totalPages,
                content = pageResult.items.map(vacancyResponseMapper::toResponse),
            ),
        )
    }

    @PostMapping
    @RequiresRole(PersonRole.TEAM_LEAD)
    fun createVacancy(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: VacancyRequest,
    ): ResponseEntity<VacancyResponse> {
        val command =
            CreateVacancyCommand(
                title = request.title,
                description = request.description,
                city = request.city,
                salary = request.salary,
                skillIds = request.skillIds.map { SkillId(it) },
            )

        val created = vacancyService.createVacancy(currentPerson, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyResponseMapper::toResponse.invoke(created))
    }

    @GetMapping("/{vacancyId}")
    fun getVacancy(
        @AuthenticationPrincipal currentPerson: Person?,
        @PathVariable vacancyId: UUID,
    ): ResponseEntity<VacancyResponse> {
        val vacancyDetails = vacancyService.getVacancy(currentPerson, VacancyId(vacancyId))
        return ResponseEntity.ok(vacancyResponseMapper.toResponse(vacancyDetails))
    }

    @PutMapping("/{vacancyId}")
    @RequiresRole(PersonRole.TEAM_LEAD)
    fun updateVacancy(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable vacancyId: UUID,
        @Valid @RequestBody request: VacancyRequest,
    ): ResponseEntity<VacancyResponse> {
        val command =
            UpdateVacancyCommand(
                title = request.title,
                description = request.description,
                city = request.city,
                salary = request.salary,
                skillIds = request.skillIds.map { SkillId(it) },
            )

        val updated = vacancyService.updateVacancy(currentPerson, VacancyId(vacancyId), command)
        return ResponseEntity.ok(vacancyResponseMapper.toResponse(updated))
    }

    @DeleteMapping("/{vacancyId}")
    @RequiresRole(PersonRole.TEAM_LEAD)
    fun deleteVacancy(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable vacancyId: UUID,
    ): ResponseEntity<Void> {
        vacancyService.deleteVacancy(currentPerson, VacancyId(vacancyId))
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{vacancyId}/status")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.HR)
    fun updateVacancyStatus(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable vacancyId: UUID,
        @Valid @RequestBody request: VacancyStatusRequest,
    ): ResponseEntity<VacancyStatusResponse> {
        val command = UpdateVacancyStatusCommand(status = request.status)
        val result = vacancyService.updateStatus(currentPerson, VacancyId(vacancyId), command)
        return ResponseEntity.ok(vacancyResponseMapper.toStatusResponse(result))
    }

    @GetMapping("/{vacancyId}/leaderboard")
    @RequiresRole(PersonRole.TEAM_LEAD, PersonRole.HR, PersonRole.ADMIN)
    fun getVacancyLeaderboard(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable vacancyId: UUID,
        @Min(0) @RequestParam(defaultValue = "0") page: Int,
        @Min(1) @Max(100) @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "1.0") motivationWeight: Double,
        @RequestParam(defaultValue = "1.0") softSkillsWeight: Double,
        @RequestParam(defaultValue = "1.0") petProjectsWeight: Double,
        @RequestParam(defaultValue = "1.0") jobExpWeight: Double,
        @RequestParam(defaultValue = "1.0") hardSkillsWeight: Double,
        @RequestParam(defaultValue = "1.0") workFormatWeight: Double,
        @RequestParam(required = false) minScore: Double?,
        @RequestParam(required = false) status: ApplicationStatus?,
        @RequestParam(required = false) skillIds: List<UUID>?,
    ): ResponseEntity<PageResponse<LeaderboardEntryResponse>> {
        val filter =
            LeaderboardFilter(
                vacancyId = VacancyId(vacancyId),
                motivationWeight = motivationWeight,
                softSkillsWeight = softSkillsWeight,
                petProjectsWeight = petProjectsWeight,
                jobExpWeight = jobExpWeight,
                hardSkillsWeight = hardSkillsWeight,
                workFormatWeight = workFormatWeight,
                minScore = minScore,
                status = status,
                skillIds = skillIds?.map { SkillId(it) }?.toSet(),
            )
        val pageQuery =
            PageQuery(
                pageNumber = page,
                pageSize = size,
                sortBy = "score",
                sortOrder = SortDirection.DESC,
            )
        val pageResult = applicationScoreService.getLeaderboard(currentPerson, filter, pageQuery)

        return ResponseEntity.ok(
            PageResponse(
                page = pageResult.pageNumber,
                size = size,
                totalElements = pageResult.totalElements,
                totalPages = pageResult.totalPages,
                content = pageResult.items.map(leaderboardResponseMapper::toResponse),
            ),
        )
    }
}
