package ru.ifmo.se.api.application.port.out

import java.time.Duration

interface FileStorage {
    fun store(
        filename: String,
        bytes: ByteArray,
    ): String

    fun storeWithKey(
        s3key: String,
        bytes: ByteArray,
    )

    fun load(s3key: String): ByteArray

    fun delete(s3key: String)

    fun getPresignedUrl(
        s3key: String,
        duration: Duration = Duration.ofHours(1),
    ): String
}
