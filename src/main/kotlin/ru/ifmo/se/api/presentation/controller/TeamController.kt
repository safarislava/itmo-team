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
import ru.ifmo.se.api.application.dto.CreateTeamCommand
import ru.ifmo.se.api.application.dto.UpdateTeamCommand
import ru.ifmo.se.api.application.service.TeamService
import ru.ifmo.se.api.domain.exception.TeamBadRequestException
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.domain.model.TeamFilter
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.TeamRequest
import ru.ifmo.se.api.presentation.dto.response.PageResponse
import ru.ifmo.se.api.presentation.dto.response.TeamResponse
import ru.ifmo.se.api.presentation.extension.toPageResponse
import ru.ifmo.se.api.presentation.extension.toSortDirectionOrNull
import ru.ifmo.se.api.presentation.mapper.TeamResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/teams")
class TeamController(
    private val teamService: TeamService,
    private val teamResponseMapper: TeamResponseMapper,
) {
    @GetMapping
    @RequiresRole(
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.DEVELOPER,
        PersonRole.MANAGER,
        PersonRole.ADMIN,
    )
    fun listTeams(
        @AuthenticationPrincipal currentPerson: Person,
        @Min(0) @RequestParam(defaultValue = "0") page: Int,
        @Min(1) @Max(100) @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") order: String,
        @RequestParam(required = false) companyId: UUID?,
        @RequestParam(required = false) name: String?,
    ): ResponseEntity<PageResponse<TeamResponse>> {
        val allowedSortColumns = setOf("id", "name")
        if (sort !in allowedSortColumns) {
            throw TeamBadRequestException("Invalid sort column: $sort. Allowed values: id, name.")
        }

        val sortDirection =
            order.toSortDirectionOrNull()
                ?: throw TeamBadRequestException("Invalid order: $order. Allowed values: asc, desc.")

        val filter =
            TeamFilter(
                companyId = companyId?.let { CompanyId(it) },
                name = name,
            )
        val pageQuery =
            PageQuery(
                pageNumber = page,
                pageSize = size,
                sortBy = sort,
                sortOrder = sortDirection,
            )

        val pageResult = teamService.listTeams(currentPerson, filter, pageQuery)
        return ResponseEntity.ok(pageResult.toPageResponse(size, teamResponseMapper::toResponse))
    }

    @PostMapping
    @RequiresRole(PersonRole.HR, PersonRole.ADMIN)
    fun createTeam(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: TeamRequest,
    ): ResponseEntity<TeamResponse> {
        val teamleadId = request.teamleadId ?: throw TeamBadRequestException("teamleadId is required.")
        val command =
            CreateTeamCommand(
                name = request.name,
                teamleadId = PersonId(teamleadId),
                companyId = request.companyId?.let { CompanyId(it) },
            )
        val created = teamService.createTeam(currentPerson, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(teamResponseMapper.toResponse(created))
    }

    @GetMapping("/{teamId}")
    @RequiresRole(
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.DEVELOPER,
        PersonRole.MANAGER,
        PersonRole.ADMIN,
    )
    fun getTeam(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable teamId: UUID,
    ): ResponseEntity<TeamResponse> {
        val team = teamService.getTeam(currentPerson, TeamId(teamId))
        return ResponseEntity.ok(teamResponseMapper.toResponse(team))
    }

    @PutMapping("/{teamId}")
    @RequiresRole(PersonRole.HR, PersonRole.ADMIN)
    fun updateTeam(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable teamId: UUID,
        @Valid @RequestBody request: TeamRequest,
    ): ResponseEntity<TeamResponse> {
        val command =
            UpdateTeamCommand(
                name = request.name,
                teamleadId = request.teamleadId?.let { PersonId(it) },
            )
        val updated = teamService.updateTeam(currentPerson, TeamId(teamId), command)
        return ResponseEntity.ok(teamResponseMapper.toResponse(updated))
    }

    @DeleteMapping("/{teamId}")
    @RequiresRole(PersonRole.HR, PersonRole.ADMIN)
    fun deleteTeam(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable teamId: UUID,
    ): ResponseEntity<Void> {
        teamService.deleteTeam(currentPerson, TeamId(teamId))
        return ResponseEntity.noContent().build()
    }
}
