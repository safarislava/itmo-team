package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.HiringApplicationsMetrics
import ru.ifmo.se.api.domain.model.HiringStatistics
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.model.VacancyStatus
import ru.ifmo.se.api.domain.repository.HiringAnalyticsRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaApplicationRepository
import ru.ifmo.se.api.infrastructure.postgres.repository.JpaVacancyRepository
import java.time.LocalDate
import java.time.ZoneId

@Repository
class HiringAnalyticsRepositoryImpl(
    private val jpaVacancyRepository: JpaVacancyRepository,
    private val jpaApplicationRepository: JpaApplicationRepository,
) : HiringAnalyticsRepository {
    @Cacheable(
        value = ["hiring_statistics"],
        key = "(#companyId ?: 'all') + ':' + (#teamId ?: 'all') + ':' + #from + ':' + #to",
        condition = "#to.isBefore(T(java.time.LocalDate).now(T(java.time.ZoneId).of('Europe/Moscow')))",
    )
    override fun getHiringStatistics(
        from: LocalDate,
        to: LocalDate,
        companyId: CompanyId?,
        teamId: TeamId?,
    ): HiringStatistics {
        val fromInstant = from.atStartOfDay(ZONE_MOSCOW).toInstant()
        val toExclusiveInstant = to.plusDays(1).atStartOfDay(ZONE_MOSCOW).toInstant()

        val openVacancies =
            when {
                teamId != null -> {
                    jpaVacancyRepository
                        .countByTeamIdAndStatusAndCreatedAtLessThan(
                            teamId.value,
                            VacancyStatus.OPEN,
                            toExclusiveInstant,
                        ).toInt()
                }

                companyId != null -> {
                    jpaVacancyRepository
                        .countByTeamCompanyIdAndStatusAndCreatedAtLessThan(
                            companyId.value,
                            VacancyStatus.OPEN,
                            toExclusiveInstant,
                        ).toInt()
                }

                else -> {
                    jpaVacancyRepository
                        .countByStatusAndCreatedAtLessThan(
                            VacancyStatus.OPEN,
                            toExclusiveInstant,
                        ).toInt()
                }
            }

        val closedVacancies =
            when {
                teamId != null -> {
                    jpaVacancyRepository
                        .countByTeamIdAndClosedAtGreaterThanEqualAndClosedAtLessThan(
                            teamId.value,
                            fromInstant,
                            toExclusiveInstant,
                        ).toInt()
                }

                companyId != null -> {
                    jpaVacancyRepository
                        .countByTeamCompanyIdAndClosedAtGreaterThanEqualAndClosedAtLessThan(
                            companyId.value,
                            fromInstant,
                            toExclusiveInstant,
                        ).toInt()
                }

                else -> {
                    jpaVacancyRepository
                        .countByClosedAtGreaterThanEqualAndClosedAtLessThan(
                            fromInstant,
                            toExclusiveInstant,
                        ).toInt()
                }
            }

        val applications =
            when {
                teamId != null -> jpaApplicationRepository.findAllByVacancyTeamId(teamId.value)
                companyId != null -> jpaApplicationRepository.findAllByVacancyTeamCompanyId(companyId.value)
                else -> jpaApplicationRepository.findAll()
            }

        val totalApplications = applications.size
        val byStatus = ApplicationStatus.entries.associateWith { status -> applications.count { it.status == status } }

        return HiringStatistics(
            from = from,
            to = to,
            openVacancies = openVacancies,
            closedVacancies = closedVacancies,
            applications =
                HiringApplicationsMetrics(
                    total = totalApplications,
                    byStatus = byStatus,
                ),
        )
    }

    companion object {
        private val ZONE_MOSCOW: ZoneId = ZoneId.of("Europe/Moscow")
    }
}
