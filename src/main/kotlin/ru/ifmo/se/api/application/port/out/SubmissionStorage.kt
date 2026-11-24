package ru.ifmo.se.api.application.port.out

interface SubmissionStorage {
    fun storeSnapshot(bytes: ByteArray): String

    fun loadSnapshot(s3key: String): ByteArray
}
