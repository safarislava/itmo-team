package ru.ifmo.se.api.application.port.out

data class ExtractedArchiveTest(
    val input: String,
    val output: String,
)

interface TestArchiveExtractor {
    fun extract(archiveBytes: ByteArray): List<ExtractedArchiveTest>
}
