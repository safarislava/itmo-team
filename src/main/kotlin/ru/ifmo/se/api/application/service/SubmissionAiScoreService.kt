package ru.ifmo.se.api.application.service

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import ru.ifmo.se.api.application.annotation.WriteDataAccess
import ru.ifmo.se.api.application.port.out.AiCodeDetector
import ru.ifmo.se.api.domain.model.SubmissionId
import ru.ifmo.se.api.domain.repository.SubmissionRepository

@Service
@WriteDataAccess
class SubmissionAiScoreService(
    private val aiCodeDetector: AiCodeDetector,
    private val submissionRepository: SubmissionRepository,
) {
    @Async
    fun checkAndSaveAiScore(
        submissionId: SubmissionId,
        sourceCode: String,
        languageName: String,
    ) {
        runCatching {
            aiCodeDetector.detect(sourceCode, languageName)?.let {
                submissionRepository.updateAiScore(submissionId, it)
            }
        }
    }
}
