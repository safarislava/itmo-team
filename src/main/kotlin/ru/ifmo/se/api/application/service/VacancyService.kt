package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.CreateVacancyCommand
import ru.ifmo.se.api.application.dto.UpdateVacancyCommand
import ru.ifmo.se.api.application.dto.UpdateVacancyStatusCommand
import ru.ifmo.se.api.application.dto.VacancyDetails
import ru.ifmo.se.api.application.dto.VacancyStatusResultDto
import ru.ifmo.se.api.application.policy.VacancyPolicy
import ru.ifmo.se.api.application.port.out.EmailService
import ru.ifmo.se.api.domain.exception.CompanyNotFoundException
import ru.ifmo.se.api.domain.exception.TeamNotFoundException
import ru.ifmo.se.api.domain.exception.VacancyBadRequestException
import ru.ifmo.se.api.domain.exception.VacancyNotFoundException
import ru.ifmo.se.api.domain.model.ApplicationId
import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.Vacancy
import ru.ifmo.se.api.domain.model.VacancyFilter
import ru.ifmo.se.api.domain.model.VacancyId
import ru.ifmo.se.api.domain.model.VacancyStatus
import ru.ifmo.se.api.domain.repository.ApplicationRepository
import ru.ifmo.se.api.domain.repository.CompanyRepository
import ru.ifmo.se.api.domain.repository.PersonRepository
import ru.ifmo.se.api.domain.repository.SkillRepository
import ru.ifmo.se.api.domain.repository.TeamRepository
import ru.ifmo.se.api.domain.repository.VacancyRepository
import java.time.Instant

@Service
class VacancyService(
    private val vacancyRepository: VacancyRepository,
    private val teamRepository: TeamRepository,
    private val companyRepository: CompanyRepository,
    private val skillRepository: SkillRepository,
    private val applicationRepository: ApplicationRepository,
    private val personRepository: PersonRepository,
    private val emailService: EmailService,
    private val vacancyPolicy: VacancyPolicy,
) {
    fun listVacancies(
        caller: Person?,
        filter: VacancyFilter,
        pageQuery: PageQuery,
    ): PageResult<VacancyDetails> {
        val scopedFilter =
            vacancyPolicy.applyFilterScope(caller, filter) ?: return PageResult(
                items = emptyList(),
                totalElements = 0,
                pageNumber = pageQuery.pageNumber,
                totalPages = 0,
            )

        val pagedVacancies = vacancyRepository.findAll(scopedFilter, pageQuery)
        val items = pagedVacancies.items.map { assembleDetails(it) }

        return PageResult(
            items = items,
            totalElements = pagedVacancies.totalElements,
            pageNumber = pagedVacancies.pageNumber,
            totalPages = pagedVacancies.totalPages,
        )
    }

    fun getVacancy(
        caller: Person?,
        id: VacancyId,
    ): VacancyDetails {
        val vacancy = vacancyRepository.findById(id) ?: throw VacancyNotFoundException()
        val team = teamRepository.findById(vacancy.teamId) ?: throw TeamNotFoundException()
        vacancyPolicy.checkCanView(caller, vacancy, team)
        return assembleDetails(vacancy)
    }

    @Transactional
    fun createVacancy(
        caller: Person,
        command: CreateVacancyCommand,
    ): VacancyDetails {
        vacancyPolicy.checkCanCreate(caller)
        val teamId = caller.teamId ?: throw VacancyBadRequestException("Team lead does not belong to any team.")
        val team = teamRepository.findById(teamId) ?: throw TeamNotFoundException()
        val company = companyRepository.findById(team.companyId) ?: throw CompanyNotFoundException()

        validateSkillIds(command.skillIds)

        val vacancy =
            Vacancy(
                id = VacancyId.generate(),
                title = command.title.trim(),
                description = command.description.trim(),
                teamId = teamId,
                city = command.city?.trim()?.takeIf { it.isNotBlank() },
                status = VacancyStatus.DRAFT,
                salary = command.salary,
                skillIds = command.skillIds,
                createdAt = Instant.now(),
                closedAt = null,
            )

        val saved = vacancyRepository.save(vacancy)
        val skills = command.skillIds.mapNotNull { skillRepository.findById(it) }
        return VacancyDetails(vacancy = saved, team = team, company = company, skills = skills)
    }

    @Transactional
    fun updateVacancy(
        caller: Person,
        id: VacancyId,
        command: UpdateVacancyCommand,
    ): VacancyDetails {
        val existing = vacancyRepository.findById(id) ?: throw VacancyNotFoundException()
        val team = teamRepository.findById(existing.teamId) ?: throw TeamNotFoundException()
        vacancyPolicy.checkCanEdit(caller, existing, team)

        validateSkillIds(command.skillIds)

        val updated =
            existing.copy(
                title = command.title.trim(),
                description = command.description.trim(),
                city = command.city?.trim()?.takeIf { it.isNotBlank() },
                salary = command.salary,
                skillIds = command.skillIds,
            )

        val saved = vacancyRepository.save(updated)
        return assembleDetails(saved)
    }

    @Transactional
    fun deleteVacancy(
        caller: Person,
        id: VacancyId,
    ) {
        val existing = vacancyRepository.findById(id) ?: throw VacancyNotFoundException()
        val team = teamRepository.findById(existing.teamId) ?: throw TeamNotFoundException()
        vacancyPolicy.checkCanDelete(caller, existing, team)

        vacancyRepository.delete(id)
    }

    @Transactional
    fun updateStatus(
        caller: Person,
        id: VacancyId,
        command: UpdateVacancyStatusCommand,
    ): VacancyStatusResultDto {
        val existing = vacancyRepository.findById(id) ?: throw VacancyNotFoundException()
        val team = teamRepository.findById(existing.teamId) ?: throw TeamNotFoundException()
        val hasContests = vacancyRepository.countContests(id) > 0

        vacancyPolicy.checkCanChangeStatus(caller, existing, team, command.status, hasContests)

        return when (command.status) {
            VacancyStatus.OPEN -> {
                val saved = vacancyRepository.save(existing.copy(status = VacancyStatus.OPEN))
                VacancyStatusResultDto(details = assembleDetails(saved), rejectedApplicationIds = emptyList())
            }

            VacancyStatus.CLOSED -> {
                val now = Instant.now()
                val saved = vacancyRepository.save(existing.copy(status = VacancyStatus.CLOSED, closedAt = now))
                val applications = applicationRepository.findAllByVacancyId(id)
                val rejectedAppIds = mutableListOf<ApplicationId>()

                for (app in applications) {
                    if (app.status != ApplicationStatus.OFFER && app.status != ApplicationStatus.REJECTED) {
                        applicationRepository.save(app.copy(status = ApplicationStatus.REJECTED))
                        rejectedAppIds.add(app.id)

                        val candidate = personRepository.findById(app.candidateId)
                        if (candidate != null) {
                            runCatching {
                                emailService.sendApplicationRejection(candidate.email, existing.title)
                            }
                        }
                    }
                }

                VacancyStatusResultDto(details = assembleDetails(saved), rejectedApplicationIds = rejectedAppIds)
            }

            else -> {
                throw VacancyBadRequestException("Unsupported status transition.")
            }
        }
    }

    private fun assembleDetails(vacancy: Vacancy): VacancyDetails {
        val team = teamRepository.findById(vacancy.teamId) ?: throw TeamNotFoundException()
        val company = companyRepository.findById(team.companyId) ?: throw CompanyNotFoundException()
        val skills = vacancy.skillIds.mapNotNull { skillRepository.findById(it) }
        return VacancyDetails(vacancy = vacancy, team = team, company = company, skills = skills)
    }

    private fun validateSkillIds(skillIds: List<ru.ifmo.se.api.domain.model.SkillId>) {
        skillIds.forEach { skillId ->
            if (skillRepository.findById(skillId) == null) {
                throw VacancyBadRequestException("Skill with id ${skillId.value} does not exist.")
            }
        }
    }
}
