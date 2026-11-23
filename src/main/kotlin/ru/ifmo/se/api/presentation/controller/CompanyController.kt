package ru.ifmo.se.api.presentation.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.dto.CreateCompanyCommand
import ru.ifmo.se.api.application.dto.UpdateCompanyCommand
import ru.ifmo.se.api.application.service.CompanyService
import ru.ifmo.se.api.domain.exception.CompanyBadRequestException
import ru.ifmo.se.api.domain.model.CompanyFilter
import ru.ifmo.se.api.domain.model.CompanyId
import ru.ifmo.se.api.domain.model.PageQuery
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.SortDirection
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.CompanyCreateRequest
import ru.ifmo.se.api.presentation.dto.request.CompanyRequest
import ru.ifmo.se.api.presentation.dto.response.CompanyResponse
import ru.ifmo.se.api.presentation.dto.response.PageResponse
import ru.ifmo.se.api.presentation.extension.toPageResponse
import ru.ifmo.se.api.presentation.extension.toSortDirectionOrNull
import ru.ifmo.se.api.presentation.mapper.CompanyResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/companies")
class CompanyController(
    private val companyService: CompanyService,
    private val companyResponseMapper: CompanyResponseMapper,
) {
    @GetMapping
    fun getCompanies(
        @Min(0) @RequestParam(defaultValue = "0") page: Int,
        @Min(1) @Max(100) @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "id") sort: String,
        @RequestParam(defaultValue = "asc") order: String,
        @RequestParam(required = false) search: String?,
    ): ResponseEntity<PageResponse<CompanyResponse>> {
        val allowedSortColumns = setOf("id", "name", "creationDate")
        if (sort !in allowedSortColumns) {
            throw CompanyBadRequestException("Invalid sort column: $sort. Allowed values: id, name, creationDate.")
        }

        val sortDirection =
            order.toSortDirectionOrNull()
                ?: throw CompanyBadRequestException("Invalid order: $order. Allowed values: asc, desc.")

        val filter = CompanyFilter(search = search)
        val pageQuery =
            PageQuery(
                pageNumber = page,
                pageSize = size,
                sortBy = sort,
                sortOrder = sortDirection,
            )

        val pageResult = companyService.getCompanies(filter, pageQuery)
        return ResponseEntity.ok(pageResult.toPageResponse(size, companyResponseMapper::toResponse))
    }

    @PostMapping
    @RequiresRole(PersonRole.ADMIN)
    fun createCompany(
        @Valid @RequestBody request: CompanyCreateRequest,
    ): ResponseEntity<CompanyResponse> {
        val command = CreateCompanyCommand(name = request.name)
        val created = companyService.createCompany(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(companyResponseMapper.toResponse(created))
    }

    @GetMapping("/{companyId}")
    fun getCompany(
        @PathVariable companyId: UUID,
    ): ResponseEntity<CompanyResponse> {
        val company = companyService.getCompany(CompanyId(companyId))
        return ResponseEntity.ok(companyResponseMapper.toResponse(company))
    }

    @PutMapping("/{companyId}")
    @RequiresRole(PersonRole.ADMIN)
    fun updateCompany(
        @PathVariable companyId: UUID,
        @Valid @RequestBody request: CompanyRequest,
    ): ResponseEntity<CompanyResponse> {
        val command = UpdateCompanyCommand(name = request.name)
        val updated = companyService.updateCompany(CompanyId(companyId), command)
        return ResponseEntity.ok(companyResponseMapper.toResponse(updated))
    }

    @DeleteMapping("/{companyId}")
    @RequiresRole(PersonRole.ADMIN)
    fun deleteCompany(
        @PathVariable companyId: UUID,
    ): ResponseEntity<Void> {
        companyService.deleteCompany(CompanyId(companyId))
        return ResponseEntity.noContent().build()
    }
}
