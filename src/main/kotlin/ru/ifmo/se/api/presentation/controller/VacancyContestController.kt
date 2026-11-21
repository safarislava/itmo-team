package ru.ifmo.se.api.presentation.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.service.VacancyContestService
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.response.ContestResponse
import ru.ifmo.se.api.presentation.mapper.ContestResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/vacancies/{vacancyId}/contests")
class VacancyContestController(
    private val vacancyContestService: VacancyContestService,
    private val contestResponseMapper: ContestResponseMapper,
) {
    @GetMapping
    fun listVacancyContests(
        @AuthenticationPrincipal currentPerson: Person?,
        @PathVariable vacancyId: UUID,
    ): ResponseEntity<List<ContestResponse>> {
        val contests = vacancyContestService.listVacancyContests(currentPerson, VacancyId(vacancyId))
        return ResponseEntity.ok(contests.map(contestResponseMapper::toResponse))
    }

    @PutMapping("/{contestId}")
    @RequiresRole(PersonRole.TEAM_LEAD)
    fun attachContestToVacancy(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable vacancyId: UUID,
        @PathVariable contestId: UUID,
    ): ResponseEntity<Void> {
        vacancyContestService.attachContestToVacancy(
            caller = currentPerson,
            vacancyId = VacancyId(vacancyId),
            contestId = ContestId(contestId),
        )
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{contestId}")
    @RequiresRole(PersonRole.TEAM_LEAD)
    fun detachContestFromVacancy(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable vacancyId: UUID,
        @PathVariable contestId: UUID,
    ): ResponseEntity<Void> {
        vacancyContestService.detachContestFromVacancy(
            caller = currentPerson,
            vacancyId = VacancyId(vacancyId),
            contestId = ContestId(contestId),
        )
        return ResponseEntity.noContent().build()
    }
}
