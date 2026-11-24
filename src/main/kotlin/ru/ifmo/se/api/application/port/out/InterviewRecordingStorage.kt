package ru.ifmo.se.api.application.port.out

import java.time.Duration

interface InterviewRecordingStorage {
    fun getPresignedUrl(
        s3key: String,
        duration: Duration = Duration.ofHours(1),
    ): String
}
