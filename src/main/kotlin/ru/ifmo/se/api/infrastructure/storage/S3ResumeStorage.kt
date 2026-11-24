package ru.ifmo.se.api.infrastructure.storage

import org.springframework.stereotype.Service
import ru.ifmo.se.api.application.port.out.ResumeStorage
import ru.ifmo.se.api.domain.model.uuidV7

@Service
class S3ResumeStorage(
    private val s3FileStorage: S3FileStorage,
) : ResumeStorage {
    override fun store(
        filename: String,
        bytes: ByteArray,
    ): String {
        val s3key = "resumes/${uuidV7()}_$filename"
        s3FileStorage.storeWithKey(s3key, bytes)
        return s3key
    }

    override fun load(s3key: String): ByteArray = s3FileStorage.load(s3key)

    override fun delete(s3key: String) = s3FileStorage.delete(s3key)
}
