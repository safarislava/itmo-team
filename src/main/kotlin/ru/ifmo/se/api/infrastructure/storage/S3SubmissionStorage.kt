package ru.ifmo.se.api.infrastructure.storage

import org.springframework.stereotype.Service
import ru.ifmo.se.api.application.port.out.SubmissionStorage
import ru.ifmo.se.api.domain.model.uuidV7

@Service
class S3SubmissionStorage(
    private val s3FileStorage: S3FileStorage,
) : SubmissionStorage {
    override fun storeSnapshot(bytes: ByteArray): String {
        val s3key = "submissions/${uuidV7()}.txt"
        s3FileStorage.storeWithKey(s3key, bytes)
        return s3key
    }

    override fun loadSnapshot(s3key: String): ByteArray = s3FileStorage.load(s3key)
}
