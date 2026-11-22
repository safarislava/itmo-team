package ru.ifmo.se.api.infrastructure.postgres.repository.adapter

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
import java.time.Instant

@Repository
class HiringAnalyticsRepositoryImpl(
    private val jpaVacancyRepository: JpaVacancyRepository,
    private val jpaApplicationRepository: JpaApplicationRepository,
) : HiringAnalyticsRepository {
    override fun getHiringStatistics(
        from: Instant,
        to: Instant,
        companyId: CompanyId?,
        teamId: TeamId?,
    ): HiringStatistics {
        val openVacancies =
            when {
                teamId != null -> {
                    jpaVacancyRepository
                        .countByTeamIdAndStatusAndCreatedAtLessThanEqual(
                            teamId.value,
                            VacancyStatus.OPEN,
                            to,
                        ).toInt()
                }

                companyId != null -> {
                    jpaVacancyRepository
                        .countByTeamCompanyIdAndStatusAndCreatedAtLessThanEqual(
                            companyId.value,
                            VacancyStatus.OPEN,
                            to,
                        ).toInt()
                }

                else -> {
                    0
                }
            }

        val closedVacancies =
            when {
                teamId != null -> {
                    jpaVacancyRepository
                        .countByTeamIdAndClosedAtGreaterThanEqualAndClosedAtLessThan(
                            teamId.value,
                            from,
                            to,
                        ).toInt()
                }

                companyId != null -> {
                    jpaVacancyRepository
                        .countByTeamCompanyIdAndClosedAtGreaterThanEqualAndClosedAtLessThan(
                            companyId.value,
                            from,
                            to,
                        ).toInt()
                }

                else -> {
                    0
                }
            }

        val applications =
            when {
                teamId != null -> jpaApplicationRepository.findAllByVacancyTeamId(teamId.value)
                companyId != null -> jpaApplicationRepository.findAllByVacancyTeamCompanyId(companyId.value)
                else -> emptyList()
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
}
