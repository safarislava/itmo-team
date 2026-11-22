package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.HiringStatistics
import ru.ifmo.se.api.domain.model.TeamId
import java.time.Instant

interface HiringAnalyticsRepository {
    fun getHiringStatistics(
        from: Instant,
        to: Instant,
        companyId: CompanyId?,
        teamId: TeamId?,
    ): HiringStatistics
}
