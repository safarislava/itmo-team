package ru.ifmo.se.api.presentation.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.service.TeamMemberService
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.response.PersonResponse
import ru.ifmo.se.api.presentation.mapper.PersonResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/teams/{teamId}/members")
class TeamMemberController(
    private val teamMemberService: TeamMemberService,
    private val personResponseMapper: PersonResponseMapper,
) {
    @GetMapping
    @RequiresRole(
        PersonRole.HR,
        PersonRole.TEAM_LEAD,
        PersonRole.MANAGER,
        PersonRole.ADMIN,
    )
    fun listTeamMembers(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable teamId: UUID,
        @RequestParam(required = false) role: PersonRole?,
    ): ResponseEntity<List<PersonResponse>> {
        val members = teamMemberService.listTeamMembers(currentPerson, TeamId(teamId), role)
        return ResponseEntity.ok(members.map(personResponseMapper::toResponse))
    }

    @PutMapping("/{personId}")
    @RequiresRole(PersonRole.HR, PersonRole.ADMIN)
    fun addTeamMember(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable teamId: UUID,
        @PathVariable personId: UUID,
    ): ResponseEntity<PersonResponse> {
        val member = teamMemberService.addTeamMember(currentPerson, TeamId(teamId), PersonId(personId))
        return ResponseEntity.ok(personResponseMapper.toResponse(member))
    }

    @DeleteMapping("/{personId}")
    @RequiresRole(PersonRole.HR, PersonRole.ADMIN)
    fun removeTeamMember(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable teamId: UUID,
        @PathVariable personId: UUID,
    ): ResponseEntity<Void> {
        teamMemberService.removeTeamMember(currentPerson, TeamId(teamId), PersonId(personId))
        return ResponseEntity.noContent().build()
    }
}
