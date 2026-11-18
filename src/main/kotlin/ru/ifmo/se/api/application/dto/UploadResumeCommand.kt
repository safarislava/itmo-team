package ru.ifmo.se.api.application.dto

class UploadResumeCommand(
    val filename: String,
    val bytes: ByteArray,
)
