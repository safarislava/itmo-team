package ru.ifmo.se.api.application.port.out

import ru.ifmo.se.api.domain.model.TestId

data class TestStorageKeys(
    val inputKey: String,
    val outputKey: String,
)

interface TestStorage {
    fun save(
        testId: TestId,
        input: String,
        output: String,
    ): TestStorageKeys

    fun loadInput(inputKey: String): String

    fun loadOutput(outputKey: String): String

    fun delete(
        inputKey: String,
        outputKey: String,
    )
}
