package ru.ifmo.se.api.presentation.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.se.api.application.dto.LanguageCommand
import ru.ifmo.se.api.application.service.LanguageService
import ru.ifmo.se.api.domain.model.LanguageId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.infrastructure.security.annotation.Authenticated
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.request.LanguageRequest
import ru.ifmo.se.api.presentation.dto.response.LanguageResponse
import ru.ifmo.se.api.presentation.mapper.LanguageResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/languages")
class LanguageController(
    private val languageService: LanguageService,
    private val languageResponseMapper: LanguageResponseMapper,
) {
    @GetMapping
    @Authenticated
    fun listLanguages(): ResponseEntity<List<LanguageResponse>> {
        val languages = languageService.listLanguages()
        return ResponseEntity.ok(languages.map(languageResponseMapper::toResponse))
    }

    @GetMapping("/{languageId}")
    @Authenticated
    fun getLanguage(
        @PathVariable languageId: UUID,
    ): ResponseEntity<LanguageResponse> {
        val language = languageService.getLanguage(LanguageId(languageId))
        return ResponseEntity.ok(languageResponseMapper.toResponse(language))
    }

    @PostMapping
    @RequiresRole(PersonRole.ADMIN)
    fun createLanguage(
        @AuthenticationPrincipal currentPerson: Person,
        @Valid @RequestBody request: LanguageRequest,
    ): ResponseEntity<LanguageResponse> {
        val command =
            LanguageCommand(
                name = request.name,
                version = request.version,
                sourceFilename = request.sourceFilename,
                compileCommand = request.compileCommand,
                runCommand = request.runCommand,
            )
        val created = languageService.createLanguage(currentPerson, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(languageResponseMapper.toResponse(created))
    }

    @PutMapping("/{languageId}")
    @RequiresRole(PersonRole.ADMIN)
    fun updateLanguage(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable languageId: UUID,
        @Valid @RequestBody request: LanguageRequest,
    ): ResponseEntity<LanguageResponse> {
        val command =
            LanguageCommand(
                name = request.name,
                version = request.version,
                sourceFilename = request.sourceFilename,
                compileCommand = request.compileCommand,
                runCommand = request.runCommand,
            )
        val updated = languageService.updateLanguage(currentPerson, LanguageId(languageId), command)
        return ResponseEntity.ok(languageResponseMapper.toResponse(updated))
    }

    @DeleteMapping("/{languageId}")
    @RequiresRole(PersonRole.ADMIN)
    fun deleteLanguage(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable languageId: UUID,
    ): ResponseEntity<Void> {
        languageService.deleteLanguage(currentPerson, LanguageId(languageId))
        return ResponseEntity.noContent().build()
    }
}
