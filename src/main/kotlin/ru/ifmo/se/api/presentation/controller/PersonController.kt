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
import ru.ifmo.se.api.application.dto.ChangePasswordCommand
import ru.ifmo.se.api.application.dto.CreatePersonCommand
import ru.ifmo.se.api.application.dto.UpdatePersonCommand
import ru.ifmo.se.api.application.dto.UpdateProfileCommand
import ru.ifmo.se.api.application.service.PersonService
import ru.ifmo.se.api.domain.exception.PersonBadRequestException
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonFilter
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.infrastructure.security.annotation.Authenticated
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.PasswordChangeRequest
import ru.ifmo.se.api.presentation.dto.request.PersonCreateRequest
import ru.ifmo.se.api.presentation.dto.request.PersonUpdateRequest
import ru.ifmo.se.api.presentation.dto.request.ProfileRequest
import ru.ifmo.se.api.presentation.dto.response.PageResponse
import ru.ifmo.se.api.presentation.dto.response.PersonResponse
import ru.ifmo.se.api.presentation.mapper.PersonResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/persons")
class PersonController(
    private val personService: PersonService,
    private val personResponseMapper: PersonResponseMapper,
) {
    @GetMapping
    @RequiresRole(PersonRole.HR, PersonRole.ADMIN)
    fun getPersons(
        @AuthenticationPrincipal currentPerson: Person,
        @Min(0) @RequestParam(defaultValue = "0") page: Int,
        @Min(1) @Max(100) @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") order: String,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) role: PersonRole?,
        @RequestParam(required = false) companyId: UUID?,
        @RequestParam(required = false) teamId: UUID?,
    ): ResponseEntity<PageResponse<PersonResponse>> {
        val allowedSorts = setOf("id", "name", "email", "role")
        if (sort !in allowedSorts) {
            throw PersonBadRequestException("Invalid sort column: $sort. Allowed: $allowedSorts")
        }

        val sortDirection =
            when (order.lowercase()) {
                "asc" -> SortDirection.ASC
                "desc" -> SortDirection.DESC
                else -> throw PersonBadRequestException("Invalid order: $order. Allowed: asc, desc")
            }

        val filter =
            PersonFilter(
                search = search,
                role = role,
                companyId = companyId?.let { CompanyId(it) },
                teamId = teamId?.let { TeamId(it) },
            )

        val pageQuery =
            PageQuery(
                pageNumber = page,
                pageSize = size,
                sortBy = sort,
                sortOrder = sortDirection,
            )

        val pageResult = personService.getPersons(currentPerson, filter, pageQuery)
        val content = pageResult.items.map(personResponseMapper::toResponse)

        val response =
            PageResponse(
                page = pageResult.pageNumber,
                size = size,
                totalElements = pageResult.totalElements,
                totalPages = pageResult.totalPages,
                content = content,
            )

        return ResponseEntity.ok(response)
    }

    @PostMapping
    @RequiresRole(PersonRole.HR, PersonRole.ADMIN)
    fun createPerson(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: PersonCreateRequest,
    ): ResponseEntity<PersonResponse> {
        val command =
            CreatePersonCommand(
                name = request.name,
                email = request.email,
                role = request.role,
                companyId = request.companyId?.let { CompanyId(it) },
                teamId = request.teamId?.let { TeamId(it) },
            )

        val created = personService.createPerson(currentPerson, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(personResponseMapper::toResponse.invoke(created))
    }

    @GetMapping("/me")
    @Authenticated
    fun getCurrentPerson(
        @AuthenticationPrincipal currentPerson: Person,
    ): ResponseEntity<PersonResponse> = ResponseEntity.ok(personResponseMapper.toResponse(currentPerson))

    @PutMapping("/me")
    @Authenticated
    fun updateCurrentPerson(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: ProfileRequest,
    ): ResponseEntity<PersonResponse> {
        val command =
            UpdateProfileCommand(
                name = request.name,
                email = request.email,
                githubId = request.githubId,
            )
        val updated = personService.updateCurrentPerson(currentPerson, command)
        return ResponseEntity.ok(personResponseMapper.toResponse(updated))
    }

    @PutMapping("/me/password")
    @Authenticated
    fun changePassword(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: PasswordChangeRequest,
    ): ResponseEntity<Void> {
        val command =
            ChangePasswordCommand(
                currentPassword = request.currentPassword,
                newPassword = request.newPassword,
            )
        personService.changePassword(currentPerson, command)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/me")
    @Authenticated
    fun deleteCurrentPerson(
        @AuthenticationPrincipal currentPerson: Person,
    ): ResponseEntity<Void> {
        personService.deletePerson(currentPerson, currentPerson.id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{personId}")
    @RequiresRole(PersonRole.HR, PersonRole.TEAM_LEAD, PersonRole.MANAGER, PersonRole.ADMIN)
    fun getPerson(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable personId: UUID,
    ): ResponseEntity<PersonResponse> {
        val person = personService.getPerson(currentPerson, PersonId(personId))
        return ResponseEntity.ok(personResponseMapper.toResponse(person))
    }

    @PutMapping("/{personId}")
    @RequiresRole(PersonRole.HR, PersonRole.ADMIN)
    fun updatePerson(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable personId: UUID,
        @Valid @RequestBody request: PersonUpdateRequest,
    ): ResponseEntity<PersonResponse> {
        val command =
            UpdatePersonCommand(
                role = request.role,
                companyId = request.companyId?.let { CompanyId(it) },
                teamId = request.teamId?.let { TeamId(it) },
            )
        val updated = personService.updatePerson(currentPerson, PersonId(personId), command)
        return ResponseEntity.ok(personResponseMapper.toResponse(updated))
    }

    @DeleteMapping("/{personId}")
    @RequiresRole(PersonRole.ADMIN)
    fun deletePerson(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable personId: UUID,
    ): ResponseEntity<Void> {
        personService.deletePerson(currentPerson, PersonId(personId))
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{personId}/dismiss")
    @RequiresRole(PersonRole.HR, PersonRole.ADMIN)
    fun dismissPerson(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable personId: UUID,
    ): ResponseEntity<Void> {
        personService.dismissPerson(currentPerson, PersonId(personId))
        return ResponseEntity.noContent().build()
    }
}
