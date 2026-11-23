package ru.ifmo.se.api.application.port.out

interface AiCodeDetector {
    fun detect(
        sourceCode: String,
        languageName: String,
    ): Short?
}
