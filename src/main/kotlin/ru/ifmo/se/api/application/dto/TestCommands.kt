package ru.ifmo.se.api.application.dto

data class CreateTestCommand(
    val input: String,
    val output: String,
    val hidden: Boolean,
)

data class UpdateTestCommand(
    val input: String,
    val output: String,
    val hidden: Boolean,
)

data class ReplaceTestsCommand(
    val bytes: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReplaceTestsCommand) return false
        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int = bytes.contentHashCode()
}
