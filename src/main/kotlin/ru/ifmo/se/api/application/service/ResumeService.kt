package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.annotation.ReadOnlyDataAccess
import ru.ifmo.se.api.application.annotation.WriteDataAccess
import ru.ifmo.se.api.application.dto.UploadResumeCommand
import ru.ifmo.se.api.application.policy.ResumePolicy
import ru.ifmo.se.api.application.port.out.FileStorage
import ru.ifmo.se.api.domain.exception.ResumeLockedException
import ru.ifmo.se.api.domain.exception.ResumeNotFoundException
import ru.ifmo.se.api.domain.model.ApplicationStatus
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.Resume
import ru.ifmo.se.api.domain.model.ResumeId
import ru.ifmo.se.api.domain.repository.ApplicationRepository
import ru.ifmo.se.api.domain.repository.ResumeRepository

@Service
class ResumeService(
    private val resumeRepository: ResumeRepository,
    private val fileStorage: FileStorage,
    private val resumePolicy: ResumePolicy,
    private val applicationRepository: ApplicationRepository,
) {
    @WriteDataAccess
    @Transactional
    fun uploadResume(
        currentPerson: Person,
        command: UploadResumeCommand,
    ): Resume {
        val s3key = fileStorage.store(command.filename, command.bytes)
        val resume =
            Resume(
                id = ResumeId.generate(),
                candidateId = currentPerson.id,
                s3key = s3key,
                filename = command.filename,
            )
        return resumeRepository.save(resume)
    }

    @WriteDataAccess
    @Transactional
    fun updateResume(
        currentPerson: Person,
        resumeId: ResumeId,
        command: UploadResumeCommand,
    ): Resume {
        val resume = resumeRepository.findById(resumeId) ?: throw ResumeNotFoundException()
        resumePolicy.checkCanUpdate(currentPerson, resume)

        val applications = applicationRepository.findAllByResumeId(resumeId)
        val isLocked = applications.any { app -> app.status != ApplicationStatus.NEW }
        if (isLocked) {
            throw ResumeLockedException()
        }

        val newS3Key = fileStorage.store(command.filename, command.bytes)
        try {
            fileStorage.delete(resume.s3key)
        } catch (_: Exception) {
            // best-effort cleanup of previous file
        }

        val updated =
            resume.copy(
                s3key = newS3Key,
                filename = command.filename,
            )
        return resumeRepository.save(updated)
    }

    @ReadOnlyDataAccess
    fun getResume(
        currentPerson: Person,
        resumeId: ResumeId,
    ): Resume {
        val resume = resumeRepository.findById(resumeId) ?: throw ResumeNotFoundException()
        resumePolicy.checkCanAccess(currentPerson, resume)
        return resume
    }

    @ReadOnlyDataAccess
    fun downloadResume(
        currentPerson: Person,
        resumeId: ResumeId,
    ): Pair<Resume, ByteArray> {
        val resume = resumeRepository.findById(resumeId) ?: throw ResumeNotFoundException()
        resumePolicy.checkCanAccess(currentPerson, resume)
        val bytes = fileStorage.load(resume.s3key)
        return resume to bytes
    }
}
