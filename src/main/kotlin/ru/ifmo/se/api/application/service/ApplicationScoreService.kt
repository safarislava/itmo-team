package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.annotation.ReadOnlyDataAccess
import ru.ifmo.se.api.application.policy.ApplicationScorePolicy
import ru.ifmo.se.api.domain.exception.ApplicationNotFoundException
import ru.ifmo.se.api.domain.exception.ApplicationScoreExceptions
import ru.ifmo.se.api.domain.exception.VacancyNotFoundException
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationScore
import ru.ifmo.se.api.domain.model.LeaderboardEntry
import ru.ifmo.se.api.domain.model.LeaderboardFilter
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.repository.ApplicationRepository
import ru.ifmo.se.api.domain.repository.ApplicationScoreRepository
import ru.ifmo.se.api.domain.repository.TeamRepository
import ru.ifmo.se.api.domain.repository.VacancyRepository

@Service
@ReadOnlyDataAccess
class ApplicationScoreService(
    private val applicationScoreRepository: ApplicationScoreRepository,
    private val applicationRepository: ApplicationRepository,
    private val vacancyRepository: VacancyRepository,
    private val teamRepository: TeamRepository,
    private val applicationScorePolicy: ApplicationScorePolicy,
) {
    @Transactional(readOnly = true)
    fun getScore(
        caller: Person,
        applicationId: ApplicationId,
    ): ApplicationScore {
        val application =
            applicationRepository.findById(applicationId)
                ?: throw ApplicationNotFoundException("Application with id ${applicationId.value} not found.")

        val vacancy =
            vacancyRepository.findById(application.vacancyId)
                ?: throw VacancyNotFoundException("Vacancy not found.")

        val team =
            teamRepository.findById(vacancy.teamId)
                ?: throw VacancyNotFoundException("Team not found.")

        applicationScorePolicy.checkCanViewScore(caller, team)

        return applicationScoreRepository.findByApplicationId(applicationId)
            ?: throw ApplicationScoreExceptions("Score not found for application ${applicationId.value}.")
    }

    @Transactional(readOnly = true)
    fun getLeaderboard(
        caller: Person,
        filter: LeaderboardFilter,
        pageQuery: PageQuery,
    ): PageResult<LeaderboardEntry> {
        val vacancy =
            vacancyRepository.findById(filter.vacancyId)
                ?: throw VacancyNotFoundException("Vacancy with id ${filter.vacancyId.value} not found.")

        val team =
            teamRepository.findById(vacancy.teamId)
                ?: throw VacancyNotFoundException("Team not found.")

        applicationScorePolicy.checkCanViewLeaderboard(caller, team)

        return applicationScoreRepository.getLeaderboard(filter, pageQuery)
    }
}
