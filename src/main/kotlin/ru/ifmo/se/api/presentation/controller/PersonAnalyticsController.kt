package ru.ifmo.se.api.presentation.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.service.PersonAnalyticsService
import ru.ifmo.se.api.domain.model.Granularity
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.response.PersonStatisticsResponse
import ru.ifmo.se.api.presentation.mapper.AnalyticsResponseMapper
import java.time.LocalDate
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/analytics/persons")
class PersonAnalyticsController(
    private val personAnalyticsService: PersonAnalyticsService,
    private val analyticsResponseMapper: AnalyticsResponseMapper,
) {
    @GetMapping("/{personId}/statistics")
    @RequiresRole(PersonRole.DEVELOPER, PersonRole.TEAM_LEAD, PersonRole.MANAGER, PersonRole.ADMIN)
    fun getPersonStatistics(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable personId: UUID,
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate,
        @RequestParam(defaultValue = "DAY") granularity: Granularity,
    ): ResponseEntity<PersonStatisticsResponse> {
        val stats =
            personAnalyticsService.getPersonStatistics(
                caller = currentPerson,
                personId = PersonId(personId),
                from = from,
                to = to,
                granularity = granularity,
            )
        return ResponseEntity.ok(analyticsResponseMapper.toResponse(stats))
    }
}
