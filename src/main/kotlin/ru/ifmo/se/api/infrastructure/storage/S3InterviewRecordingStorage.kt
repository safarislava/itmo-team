package ru.ifmo.se.api.infrastructure.storage

import org.springframework.stereotype.Service
import ru.ifmo.se.api.application.port.out.InterviewRecordingStorage
import java.time.Duration

@Service
class S3InterviewRecordingStorage(
    private val s3FileStorage: S3FileStorage,
) : InterviewRecordingStorage {
    override fun getPresignedUrl(
        s3key: String,
        duration: Duration,
    ): String = s3FileStorage.getPresignedUrl(s3key, duration)
}
