package ru.ifmo.se.api.infrastructure.storage

import org.springframework.stereotype.Service
import ru.ifmo.se.api.application.port.out.TestStorage
import ru.ifmo.se.api.application.port.out.TestStorageKeys
import ru.ifmo.se.api.domain.model.TestId

@Service
class S3TestStorage(
    private val s3FileStorage: S3FileStorage,
) : TestStorage {
    override fun save(
        testId: TestId,
        input: String,
        output: String,
    ): TestStorageKeys {
        val inputKey = "${testId.value}.in"
        val outputKey = "${testId.value}.out"
        s3FileStorage.storeWithKey(inputKey, input.toByteArray(Charsets.UTF_8))
        s3FileStorage.storeWithKey(outputKey, output.toByteArray(Charsets.UTF_8))
        return TestStorageKeys(inputKey, outputKey)
    }

    override fun loadInput(inputKey: String): String = s3FileStorage.load(inputKey).toString(Charsets.UTF_8)

    override fun loadOutput(outputKey: String): String = s3FileStorage.load(outputKey).toString(Charsets.UTF_8)

    override fun delete(
        inputKey: String,
        outputKey: String,
    ) {
        s3FileStorage.delete(inputKey)
        s3FileStorage.delete(outputKey)
    }
}
