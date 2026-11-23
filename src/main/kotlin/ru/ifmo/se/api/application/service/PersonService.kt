package ru.ifmo.se.api.application.service

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.ChangePasswordCommand
import ru.ifmo.se.api.application.dto.CreatePersonCommand
import ru.ifmo.se.api.application.dto.UpdatePersonCommand
import ru.ifmo.se.api.application.dto.UpdateProfileCommand
import ru.ifmo.se.api.application.policy.PersonPolicy
import ru.ifmo.se.api.application.port.out.EmailService
import ru.ifmo.se.api.application.port.out.PasswordHasher
import ru.ifmo.se.api.domain.exception.InvalidCurrentPasswordException
import ru.ifmo.se.api.domain.exception.PersonBadRequestException
import ru.ifmo.se.api.domain.exception.PersonNotFoundException
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonFilter
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.TeamId
import ru.ifmo.se.api.domain.repository.CompanyRepository
import ru.ifmo.se.api.domain.repository.PersonRepository
import ru.ifmo.se.api.domain.repository.RefreshTokenRepository
import ru.ifmo.se.api.domain.repository.TeamRepository
import java.security.SecureRandom

@Service
class PersonService(
    private val personRepository: PersonRepository,
    private val companyRepository: CompanyRepository,
    private val teamRepository: TeamRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordHasher: PasswordHasher,
    private val emailService: EmailService,
    private val personPolicy: PersonPolicy,
) {
    private val random = SecureRandom()
    private val passwordChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"

    fun getPersons(
        caller: Person,
        filter: PersonFilter,
        page: PageQuery,
    ): PageResult<Person> {
        val effectiveFilter =
            personPolicy.applyFilterScope(caller, filter)
                ?: return PageResult.empty(page.pageNumber)

        return personRepository.findByFilter(effectiveFilter, page)
    }

    fun getPerson(
        caller: Person,
        targetPersonId: PersonId,
    ): Person {
        val target = personRepository.findById(targetPersonId) ?: throw PersonNotFoundException()
        personPolicy.checkCanView(caller, target)
        return target
    }

    @Transactional
    fun createPerson(
        caller: Person,
        command: CreatePersonCommand,
    ): Person {
        if (command.role == PersonRole.CANDIDATE) {
            throw PersonBadRequestException("CANDIDATE is not allowed for internal users.")
        }

        personPolicy.checkCanCreate(caller, command)

        val (companyId, teamId) = resolvePlacement(caller, command)
        val temporaryPassword = generateTemporaryPassword()

        val newPerson =
            Person(
                id = PersonId.generate(),
                name = command.name,
                email = command.email,
                passwordHash = passwordHasher.hash(temporaryPassword),
                role = command.role,
                companyId = companyId,
                teamId = teamId,
                githubId = null,
            )

        val saved = personRepository.save(newPerson)
        emailService.sendTemporaryPassword(saved.email, temporaryPassword)
        return saved
    }

    private fun resolvePlacement(
        caller: Person,
        command: CreatePersonCommand,
    ): Pair<CompanyId?, TeamId?> =
        when (caller.role) {
            PersonRole.HR -> resolveHrPlacement(caller, command)
            PersonRole.ADMIN -> resolveAdminPlacement(command)
            else -> throw AccessDeniedException("Access is denied.")
        }

    private fun resolveHrPlacement(
        caller: Person,
        command: CreatePersonCommand,
    ): Pair<CompanyId?, TeamId?> {
        val companyId = caller.companyId ?: throw PersonBadRequestException("HR has no company assigned.")
        if (command.companyId != null && command.companyId != companyId) {
            throw PersonBadRequestException("HR cannot specify a different companyId.")
        }
        validateRoleAndTeam(command.role, command.teamId, companyId)
        return companyId to command.teamId
    }

    private fun resolveAdminPlacement(command: CreatePersonCommand): Pair<CompanyId?, TeamId?> {
        if (command.role == PersonRole.ADMIN) {
            if (command.companyId != null) {
                throw PersonBadRequestException("companyId is not allowed for ADMIN.")
            }
            if (command.teamId != null) {
                throw PersonBadRequestException("teamId is not allowed for ADMIN.")
            }
            return null to null
        }
        val companyId =
            command.companyId ?: throw PersonBadRequestException("companyId is required for non-ADMIN users.")
        if (companyRepository.findById(companyId) == null) {
            throw PersonBadRequestException("Company with id ${companyId.value} not found.")
        }
        validateRoleAndTeam(command.role, command.teamId, companyId)
        return companyId to command.teamId
    }

    private fun validateRoleAndTeam(
        role: PersonRole,
        teamId: TeamId?,
        companyId: CompanyId,
    ) {
        when (role) {
            PersonRole.DEVELOPER -> {
                val nonNullTeamId = teamId ?: throw PersonBadRequestException("teamId is required for DEVELOPER.")
                validateTeamBelongsToCompany(nonNullTeamId, companyId)
            }

            PersonRole.HR, PersonRole.MANAGER, PersonRole.TEAM_LEAD -> {
                if (teamId != null) {
                    throw PersonBadRequestException("teamId is not allowed for $role.")
                }
            }

            PersonRole.ADMIN -> {
                if (teamId != null) {
                    throw PersonBadRequestException("teamId is not allowed for ADMIN.")
                }
            }

            PersonRole.CANDIDATE -> {
                throw PersonBadRequestException("CANDIDATE is not allowed for internal users.")
            }
        }
    }

    private fun validateTeamBelongsToCompany(
        teamId: TeamId,
        companyId: CompanyId,
    ) {
        val team = teamRepository.findById(teamId) ?: throw PersonBadRequestException("Team not found.")
        if (team.companyId != companyId) {
            throw PersonBadRequestException("Team does not belong to the specified company.")
        }
    }

    @Transactional
    fun updatePerson(
        caller: Person,
        targetPersonId: PersonId,
        command: UpdatePersonCommand,
    ): Person {
        val target = personRepository.findById(targetPersonId) ?: throw PersonNotFoundException()

        if (command.role == PersonRole.CANDIDATE) {
            throw PersonBadRequestException("CANDIDATE can not be assigned to internal users.")
        }

        personPolicy.checkCanUpdate(caller, target, command)

        val (companyId, teamId) = resolvePlacement(caller, target, command)

        val updated =
            target.copy(
                role = command.role,
                companyId = companyId,
                teamId = teamId,
            )

        return personRepository.save(updated)
    }

    private fun resolvePlacement(
        caller: Person,
        target: Person,
        command: UpdatePersonCommand,
    ): Pair<CompanyId?, TeamId?> =
        when (caller.role) {
            PersonRole.HR -> resolveHrPlacement(caller, target, command)
            PersonRole.ADMIN -> resolveAdminPlacement(target, command)
            else -> throw AccessDeniedException("Access is denied.")
        }

    private fun resolveHrPlacement(
        caller: Person,
        target: Person,
        command: UpdatePersonCommand,
    ): Pair<CompanyId?, TeamId?> {
        val hrCompanyId = caller.companyId ?: throw PersonBadRequestException("HR has no company assigned.")
        validateRoleAndTeam(command.role, command.teamId, hrCompanyId)
        return hrCompanyId to command.teamId
    }

    private fun resolveAdminPlacement(
        target: Person,
        command: UpdatePersonCommand,
    ): Pair<CompanyId?, TeamId?> {
        if (command.role == PersonRole.ADMIN) {
            if (command.companyId != null) {
                throw PersonBadRequestException("companyId is not allowed for ADMIN.")
            }
            if (command.teamId != null) {
                throw PersonBadRequestException("teamId is not allowed for ADMIN.")
            }
            return null to null
        }
        val companyId =
            target.companyId
                ?: command.companyId
                ?: throw PersonBadRequestException("companyId is required when assigning a company role.")
        if (companyRepository.findById(companyId) == null) {
            throw PersonBadRequestException("Company with id ${companyId.value} not found.")
        }
        validateRoleAndTeam(command.role, command.teamId, companyId)
        return companyId to command.teamId
    }

    @Transactional
    fun updateCurrentPerson(
        currentPerson: Person,
        command: UpdateProfileCommand,
    ): Person {
        val updated =
            currentPerson.copy(
                name = command.name,
                email = command.email,
                githubId = command.githubId,
            )

        return personRepository.save(updated)
    }

    @Transactional
    fun changePassword(
        currentPerson: Person,
        command: ChangePasswordCommand,
    ) {
        val person = personRepository.findById(currentPerson.id) ?: throw PersonNotFoundException()

        if (!passwordHasher.matches(command.currentPassword, person.passwordHash)) {
            throw InvalidCurrentPasswordException()
        }

        val newPasswordHash = passwordHasher.hash(command.newPassword)
        val updated = person.copy(passwordHash = newPasswordHash)

        personRepository.save(updated)
        refreshTokenRepository.revokeAllByPersonId(person.id)
    }

    @Transactional
    fun deletePerson(
        caller: Person,
        targetPersonId: PersonId,
    ) {
        personPolicy.checkCanDelete(caller, targetPersonId)

        val target = personRepository.findById(targetPersonId) ?: throw PersonNotFoundException()
        refreshTokenRepository.revokeAllByPersonId(target.id)
        personRepository.delete(target)
    }

    @Transactional
    fun dismissPerson(
        caller: Person,
        targetPersonId: PersonId,
    ) {
        val target = personRepository.findById(targetPersonId) ?: throw PersonNotFoundException()

        if (target.role == PersonRole.CANDIDATE) {
            throw PersonBadRequestException("Candidate is not employed and cannot be dismissed.")
        }

        personPolicy.checkCanDismiss(caller, target)

        val dismissed =
            target.copy(
                role = PersonRole.CANDIDATE,
                companyId = null,
                teamId = null,
            )

        personRepository.save(dismissed)
        refreshTokenRepository.revokeAllByPersonId(target.id)
    }

    private fun generateTemporaryPassword(): String =
        (1..12)
            .map { passwordChars[random.nextInt(passwordChars.length)] }
            .joinToString("")
}
