package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.annotation.ReadOnlyDataAccess
import ru.ifmo.se.api.application.policy.HiringAnalyticsPolicy
import ru.ifmo.se.api.domain.exception.AnalyticsNotFoundException
import ru.ifmo.se.api.domain.model.HiringStatistics
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.repository.CompanyRepository
import ru.ifmo.se.api.domain.repository.HiringAnalyticsRepository
import ru.ifmo.se.api.domain.repository.TeamRepository
import java.time.Instant

@Service
@Transactional(readOnly = true)
@ReadOnlyDataAccess
class HiringAnalyticsService(
    private val hiringAnalyticsRepository: HiringAnalyticsRepository,
    private val companyRepository: CompanyRepository,
    private val teamRepository: TeamRepository,
    private val hiringAnalyticsPolicy: HiringAnalyticsPolicy,
) {
    fun getHiringStatistics(
        caller: Person,
        from: Instant,
        to: Instant,
        teamId: TeamId?,
    ): HiringStatistics {
        hiringAnalyticsPolicy.checkInterval(from, to)

        val team =
            teamId?.let {
                teamRepository.findById(it) ?: throw AnalyticsNotFoundException("Team not found.")
            }

        hiringAnalyticsPolicy.checkCanView(caller, team)

        val companyId = caller.companyId!!
        companyRepository.findById(companyId)
            ?: throw AnalyticsNotFoundException("Company not found.")

        return hiringAnalyticsRepository.getHiringStatistics(
            from = from,
            to = to,
            companyId = companyId,
            teamId = teamId,
        )
    }
}
