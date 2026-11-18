package ru.ifmo.se.api.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ifmo.se.api.application.dto.UploadResumeCommand
import ru.ifmo.se.api.application.policy.ResumePolicy
import ru.ifmo.se.api.application.port.out.FileStorage
import ru.ifmo.se.api.domain.exception.ResumeNotFoundException
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.Resume
import ru.ifmo.se.api.domain.model.ResumeId
import ru.ifmo.se.api.domain.repository.ResumeRepository

@Service
class ResumeService(
    private val resumeRepository: ResumeRepository,
    private val fileStorage: FileStorage,
    private val resumePolicy: ResumePolicy,
) {
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

    fun getResume(
        currentPerson: Person,
        resumeId: ResumeId,
    ): Resume {
        val resume = resumeRepository.findById(resumeId) ?: throw ResumeNotFoundException()
        resumePolicy.checkCanAccess(currentPerson, resume)
        return resume
    }

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
