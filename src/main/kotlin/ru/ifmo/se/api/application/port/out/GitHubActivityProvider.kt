package ru.ifmo.se.api.application.port.out

import ru.ifmo.se.api.domain.model.ActivityMetrics
import java.time.LocalDate

interface GitHubActivityProvider {
    fun fetchDailyActivity(
        githubId: Long,
        date: LocalDate,
    ): ActivityMetrics
}
