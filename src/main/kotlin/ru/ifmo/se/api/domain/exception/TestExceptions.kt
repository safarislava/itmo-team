package ru.ifmo.se.api.domain.exception

class TestNotFoundException(
    message: String = "Test not found.",
) : RuntimeException(message)

class TestBadRequestException(
    message: String = "The archive is malformed or a file has no pair.",
) : RuntimeException(message)
