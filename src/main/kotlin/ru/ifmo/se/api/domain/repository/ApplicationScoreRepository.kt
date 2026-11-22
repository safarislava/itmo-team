package ru.ifmo.se.api.domain.repository

import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationScore
import ru.ifmo.se.api.domain.model.LeaderboardEntry
import ru.ifmo.se.api.domain.model.LeaderboardFilter
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult

interface ApplicationScoreRepository {
    fun findByApplicationId(applicationId: ApplicationId): ApplicationScore?

    fun getLeaderboard(
        filter: LeaderboardFilter,
        pageQuery: PageQuery,
    ): PageResult<LeaderboardEntry>
}
