package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.CreateCompanyCommand
import ru.ifmo.se.api.application.dto.UpdateCompanyCommand
import ru.ifmo.se.api.domain.exception.CompanyNotFoundException
import ru.ifmo.se.api.domain.model.Company
import ru.ifmo.se.api.domain.model.CompanyFilter
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PageResult
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.repository.CompanyRepository
import ru.ifmo.se.api.domain.repository.PersonRepository
import ru.ifmo.se.api.domain.repository.RefreshTokenRepository
import java.time.LocalDate

@Service
class CompanyService(
    private val companyRepository: CompanyRepository,
    private val personRepository: PersonRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun getCompanies(
        filter: CompanyFilter,
        pageQuery: PageQuery,
    ): PageResult<Company> = companyRepository.findAll(filter, pageQuery)

    fun getCompany(companyId: CompanyId): Company = companyRepository.findById(companyId) ?: throw CompanyNotFoundException()

    @Transactional
    fun createCompany(command: CreateCompanyCommand): Company {
        val company =
            Company(
                id = CompanyId.generate(),
                name = command.name.trim(),
                creationDate = LocalDate.now(),
            )
        return companyRepository.save(company)
    }

    @Transactional
    fun updateCompany(
        companyId: CompanyId,
        command: UpdateCompanyCommand,
    ): Company {
        val target = companyRepository.findById(companyId) ?: throw CompanyNotFoundException()
        val updated = target.copy(name = command.name.trim())
        return companyRepository.save(updated)
    }

    @Transactional
    fun deleteCompany(companyId: CompanyId) {
        val target = companyRepository.findById(companyId) ?: throw CompanyNotFoundException()

        val employees = personRepository.findAllByCompanyId(companyId)
        for (employee in employees) {
            refreshTokenRepository.revokeAllByPersonId(employee.id)
            val dismissedEmployee =
                employee.copy(
                    role = PersonRole.CANDIDATE,
                    companyId = null,
                    teamId = null,
                )
            personRepository.save(dismissedEmployee)
        }

        companyRepository.delete(target)
    }
}
