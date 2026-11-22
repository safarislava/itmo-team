package ru.ifmo.se.api.application.dto

data class LanguageCommand(
    val name: String,
    val version: String,
    val sourceFilename: String,
    val compileCommand: String?,
    val runCommand: String,
)
