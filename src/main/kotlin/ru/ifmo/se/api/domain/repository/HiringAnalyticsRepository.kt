package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.HiringStatistics
import ru.ifmo.se.api.domain.model.TeamId
import java.time.LocalDate

interface HiringAnalyticsRepository {
    fun getHiringStatistics(
        from: LocalDate,
        to: LocalDate,
        companyId: CompanyId?,
        teamId: TeamId?,
    ): HiringStatistics
}
