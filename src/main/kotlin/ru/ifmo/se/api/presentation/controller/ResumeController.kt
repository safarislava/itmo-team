package ru.ifmo.se.api.presentation.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.ifmo.se.api.application.dto.UploadResumeCommand
import ru.ifmo.se.api.application.service.ResumeService
import ru.ifmo.se.api.domain.exception.ResumeBadRequestException
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.domain.model.ResumeId
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole
import ru.ifmo.se.api.presentation.dto.response.ResumeResponse
import ru.ifmo.se.api.presentation.mapper.ResumeResponseMapper
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/resumes")
class ResumeController(
    private val resumeService: ResumeService,
    private val resumeResponseMapper: ResumeResponseMapper,
) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @RequiresRole(PersonRole.CANDIDATE, PersonRole.ADMIN)
    fun uploadResume(
        @AuthenticationPrincipal currentPerson: Person,
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<ResumeResponse> {
        val contentType = file.contentType
        if (contentType !in ALLOWED_CONTENT_TYPES) {
            throw ResumeBadRequestException(
                "Unsupported resume format: $contentType. Supported: PDF, PNG, JPG, DOCX.",
            )
        }

        if (file.size > MAX_FILE_SIZE) {
            throw ResumeBadRequestException("File size exceeds 5MB limit.")
        }

        val command =
            UploadResumeCommand(
                filename = file.originalFilename ?: "resume",
                bytes = file.bytes,
            )
        val created = resumeService.uploadResume(currentPerson, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(resumeResponseMapper.toResponse(created))
    }

    @GetMapping("/{resumeId}")
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.ADMIN,
    )
    fun getResume(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable resumeId: UUID,
    ): ResponseEntity<ResumeResponse> {
        val resume = resumeService.getResume(currentPerson, ResumeId(resumeId))
        return ResponseEntity.ok(resumeResponseMapper.toResponse(resume))
    }

    @GetMapping("/{resumeId}/content")
    @RequiresRole(
        PersonRole.CANDIDATE,
        PersonRole.ADMIN,
    )
    fun downloadResume(
        @AuthenticationPrincipal currentPerson: Person,
        @PathVariable resumeId: UUID,
    ): ResponseEntity<ByteArray> {
        val (resume, bytes) = resumeService.downloadResume(currentPerson, ResumeId(resumeId))
        val mediaType =
            MediaTypeFactory
                .getMediaType(resume.filename)
                .orElse(MediaType.APPLICATION_OCTET_STREAM)

        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${resume.filename}\"")
            .contentType(mediaType)
            .body(bytes)
    }

    private companion object {
        private const val MAX_FILE_SIZE = 5 * 1024 * 1024L
        private val ALLOWED_CONTENT_TYPES =
            setOf(
                "application/pdf",
                "image/png",
                "image/jpeg",
                "image/jpg",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            )
    }
}
