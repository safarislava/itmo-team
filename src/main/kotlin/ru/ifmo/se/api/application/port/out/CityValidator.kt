package ru.ifmo.se.api.application.port.out

interface CityValidator {
    fun isValid(cityName: String): Boolean
}
