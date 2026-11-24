package ru.ifmo.se.api.application.port.out

interface ResumeStorage {
    fun store(
        filename: String,
        bytes: ByteArray,
    ): String

    fun load(s3key: String): ByteArray

    fun delete(s3key: String)
}
