package ru.ifmo.se.api.infrastructure.geocoding

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import ru.ifmo.se.api.application.port.out.CityValidator

@Component
class OpenMeteoCityValidator : CityValidator {
    private val restClient = RestClient.create("https://geocoding-api.open-meteo.com")

    @Cacheable(
        value = ["cities"],
        key = "#cityName",
        condition = "#cityName != null && !#cityName.trim().isEmpty()",
    )
    override fun isValid(cityName: String): Boolean {
        val city = cityName.trim()
        if (city.isBlank()) return false

        return runCatching {
            restClient
                .get()
                .uri { builder ->
                    builder
                        .path("/v1/search")
                        .queryParam("name", city)
                        .queryParam("count", 10)
                        .apply {
                            if (city.any { it in '\u0400'..'\u04FF' }) queryParam("language", "ru")
                        }.build()
                }.retrieve()
                .body<GeocodingResponse>()
                ?.results
                ?.any { it.featureCode?.startsWith("PPL") == true && it.name.equals(city, ignoreCase = true) } == true
        }.getOrDefault(false)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GeocodingResponse(
        val results: List<GeocodingResult> = emptyList(),
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GeocodingResult(
        val name: String? = null,
        @JsonProperty("feature_code") val featureCode: String? = null,
    )
}
