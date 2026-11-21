package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.policy.VacancyContestPolicy
import ru.ifmo.se.api.domain.exception.ContestNotFoundException
import ru.ifmo.se.api.domain.exception.TeamNotFoundException
import ru.ifmo.se.api.domain.exception.VacancyContestAlreadyStartedException
import ru.ifmo.se.api.domain.exception.VacancyContestNotFoundException
import ru.ifmo.se.api.domain.exception.VacancyNotFoundException
import ru.ifmo.se.api.domain.model.Contest
import ru.ifmo.se.api.domain.model.ContestId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.domain.repository.ApplicationRepository
import ru.ifmo.se.api.domain.repository.ContestRepository
import ru.ifmo.se.api.domain.repository.TeamRepository
import ru.ifmo.se.api.domain.repository.VacancyRepository
import java.time.Instant

@Service
class VacancyContestService(
    private val vacancyRepository: VacancyRepository,
    private val contestRepository: ContestRepository,
    private val teamRepository: TeamRepository,
    private val applicationRepository: ApplicationRepository,
    private val vacancyContestPolicy: VacancyContestPolicy,
) {
    fun listVacancyContests(
        caller: Person?,
        vacancyId: VacancyId,
    ): List<Contest> {
        val vacancy = vacancyRepository.findById(vacancyId) ?: throw VacancyNotFoundException()
        val team = teamRepository.findById(vacancy.teamId) ?: throw TeamNotFoundException()
        val isCandidateApplicant =
            caller?.let { applicationRepository.findByCandidateIdAndVacancyId(it.id, vacancyId) != null } ?: false

        vacancyContestPolicy.checkCanListContests(caller, team, isCandidateApplicant)
        return contestRepository.findByVacancyId(vacancyId)
    }

    @Transactional
    fun attachContestToVacancy(
        caller: Person,
        vacancyId: VacancyId,
        contestId: ContestId,
    ) {
        val vacancy = vacancyRepository.findById(vacancyId) ?: throw VacancyNotFoundException()
        val team = teamRepository.findById(vacancy.teamId) ?: throw TeamNotFoundException()
        contestRepository.findById(contestId) ?: throw ContestNotFoundException()

        vacancyContestPolicy.checkCanAttachContest(caller, vacancy, team)

        vacancyRepository.attachContest(vacancyId, contestId)

        val applications = applicationRepository.findAllByVacancyId(vacancyId)
        applications.forEach { app ->
            contestRepository.addParticipant(contestId, app.candidateId)
        }
    }

    @Transactional
    fun detachContestFromVacancy(
        caller: Person,
        vacancyId: VacancyId,
        contestId: ContestId,
    ) {
        val vacancy = vacancyRepository.findById(vacancyId) ?: throw VacancyNotFoundException()
        val team = teamRepository.findById(vacancy.teamId) ?: throw TeamNotFoundException()
        val contest = contestRepository.findById(contestId) ?: throw ContestNotFoundException()

        if (!vacancyRepository.isContestAttached(vacancyId, contestId)) {
            throw VacancyContestNotFoundException()
        }

        if (contest.scheduledTime <= Instant.now()) {
            throw VacancyContestAlreadyStartedException()
        }

        vacancyContestPolicy.checkCanDetachContest(caller, team)
        vacancyRepository.detachContest(vacancyId, contestId)
    }
}
