package ru.ifmo.se.api.presentation.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.service.HiringAnalyticsService
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.response.HiringStatisticsResponse
import ru.ifmo.se.api.presentation.mapper.AnalyticsResponseMapper
import java.time.Instant
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/analytics/hiring")
class HiringAnalyticsController(
    private val hiringAnalyticsService: HiringAnalyticsService,
    private val analyticsResponseMapper: AnalyticsResponseMapper,
) {
    @GetMapping
    @RequiresRole(PersonRole.MANAGER, PersonRole.ADMIN)
    fun getHiringStatistics(
        @AuthenticationPrincipal currentPerson: Person,
        @RequestParam from: Instant,
        @RequestParam to: Instant,
        @RequestParam(required = false) teamId: UUID?,
        @RequestParam(required = false) companyId: UUID?,
    ): ResponseEntity<HiringStatisticsResponse> {
        val stats =
            hiringAnalyticsService.getHiringStatistics(
                caller = currentPerson,
                from = from,
                to = to,
                companyId = companyId?.let { CompanyId(it) },
                teamId = teamId?.let { TeamId(it) },
            )
        return ResponseEntity.ok(analyticsResponseMapper.toResponse(stats))
    }
}
