package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.LanguageCommand
import ru.ifmo.se.api.application.policy.LanguagePolicy
import ru.ifmo.se.api.domain.exception.LanguageNotFoundException
import ru.ifmo.se.api.domain.model.Language
import ru.ifmo.se.api.domain.model.LanguageId
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.repository.LanguageRepository

@Service
class LanguageService(
    private val languageRepository: LanguageRepository,
    private val languagePolicy: LanguagePolicy,
) {
    fun listLanguages(): List<Language> = languageRepository.findAll()

    fun getLanguage(id: LanguageId): Language = languageRepository.findById(id) ?: throw LanguageNotFoundException()

    @Transactional
    fun createLanguage(
        caller: Person,
        command: LanguageCommand,
    ): Language {
        languagePolicy.checkCanManage(caller)
        val language =
            Language(
                id = LanguageId.generate(),
                name = command.name.trim(),
                version = command.version.trim(),
                sourceFilename = command.sourceFilename.trim(),
                compileCommand = command.compileCommand?.trim()?.takeIf { it.isNotBlank() },
                runCommand = command.runCommand.trim(),
            )
        return languageRepository.save(language)
    }

    @Transactional
    fun updateLanguage(
        caller: Person,
        id: LanguageId,
        command: LanguageCommand,
    ): Language {
        languagePolicy.checkCanManage(caller)
        val existing = languageRepository.findById(id) ?: throw LanguageNotFoundException()
        val updated =
            existing.copy(
                name = command.name.trim(),
                version = command.version.trim(),
                sourceFilename = command.sourceFilename.trim(),
                compileCommand = command.compileCommand?.trim()?.takeIf { it.isNotBlank() },
                runCommand = command.runCommand.trim(),
            )
        return languageRepository.save(updated)
    }

    @Transactional
    fun deleteLanguage(
        caller: Person,
        id: LanguageId,
    ) {
        languagePolicy.checkCanManage(caller)
        languageRepository.findById(id) ?: throw LanguageNotFoundException()
        languageRepository.delete(id)
    }
}
