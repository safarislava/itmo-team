package ru.ifmo.se.api.infrastructure.storage

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.ifmo.se.api.application.port.out.FileStorage
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration
import ru.ifmo.se.api.domain.model.uuidV7

@Service
class S3FileStorage(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    @Value("\${storage.s3.bucket}")
    private val bucket: String,
) : FileStorage {
    override fun store(
        filename: String,
        bytes: ByteArray,
    ): String {
        val s3key = "${uuidV7()}_$filename"
        storeWithKey(s3key, bytes)
        return s3key
    }

    override fun storeWithKey(
        s3key: String,
        bytes: ByteArray,
    ) {
        val request =
            PutObjectRequest
                .builder()
                .bucket(bucket)
                .key(s3key)
                .build()

        s3Client.putObject(request, RequestBody.fromBytes(bytes))
    }

    override fun load(s3key: String): ByteArray {
        val request =
            GetObjectRequest
                .builder()
                .bucket(bucket)
                .key(s3key)
                .build()

        return try {
            s3Client.getObjectAsBytes(request).asByteArray()
        } catch (ex: NoSuchKeyException) {
            throw IllegalArgumentException("File with key $s3key not found in storage.", ex)
        }
    }

    override fun delete(s3key: String) {
        val request =
            DeleteObjectRequest
                .builder()
                .bucket(bucket)
                .key(s3key)
                .build()

        s3Client.deleteObject(request)
    }

    override fun getPresignedUrl(
        s3key: String,
        duration: Duration,
    ): String {
        val getObjectRequest =
            GetObjectRequest
                .builder()
                .bucket(bucket)
                .key(s3key)
                .build()

        val presignRequest =
            GetObjectPresignRequest
                .builder()
                .signatureDuration(duration)
                .getObjectRequest(getObjectRequest)
                .build()

        return s3Presigner.presignGetObject(presignRequest).url().toString()
    }
}
