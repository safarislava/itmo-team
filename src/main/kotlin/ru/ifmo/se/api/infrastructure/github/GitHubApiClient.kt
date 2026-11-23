package ru.ifmo.se.api.infrastructure.github

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import ru.ifmo.se.api.application.port.out.GitHubActivityProvider
import ru.ifmo.se.api.domain.model.ActivityMetrics
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Component
class GitHubApiClient(
    @Value("\${app.github.token}") private val token: String,
    @Value("\${app.github.base-url}") private val baseUrl: String,
) : GitHubActivityProvider {
    private val restClient =
        RestClient
            .builder()
            .baseUrl(baseUrl)
            .defaultHeader("Accept", "application/vnd.github+json")
            .defaultHeader("User-Agent", "itmo-team-api")
            .let { builder ->
                if (token.isNotBlank()) builder.defaultHeader("Authorization", "Bearer $token") else builder
            }.requestFactory(
                SimpleClientHttpRequestFactory().apply {
                    setConnectTimeout(Duration.ofSeconds(5))
                    setReadTimeout(Duration.ofSeconds(15))
                },
            ).build()

    override fun fetchDailyActivity(
        githubUsername: String,
        date: LocalDate,
    ): ActivityMetrics {
        val username = githubUsername.trim().takeIf { it.isNotEmpty() } ?: return ActivityMetrics.ZERO

        return runCatching {
            generateSequence(1) { page -> (page + 1).takeIf { it <= MAX_PAGES } }
                .map { page -> fetchEventsPage(username, page) }
                .takeWhile { it.isNotEmpty() }
                .flatMap { it.asSequence() }
                .takeWhile { (it.date ?: date) >= date }
                .filter { it.date == date }
                .fold(ActivityMetrics.ZERO) { acc, event -> acc + event.toMetrics() }
        }.getOrElse {
            ActivityMetrics.ZERO
        }
    }

    private fun fetchEventsPage(
        username: String,
        page: Int,
    ): Array<GitHubEventResponse> =
        restClient
            .get()
            .uri("/users/{username}/events?per_page={perPage}&page={page}", username, PER_PAGE, page)
            .retrieve()
            .body<Array<GitHubEventResponse>>() ?: emptyArray()

    private val GitHubEventResponse.date: LocalDate?
        get() = createdAt?.let { runCatching { Instant.parse(it).atZone(ZONE_MOSCOW).toLocalDate() }.getOrNull() }

    private fun GitHubEventResponse.toMetrics(): ActivityMetrics =
        when (type) {
            "PushEvent" -> {
                val commits = payload?.distinctSize ?: payload?.size ?: payload?.commits?.size ?: 1
                ActivityMetrics.ZERO.copy(countCommits = commits)
            }

            "PullRequestEvent" -> {
                when (payload?.action) {
                    "opened" -> ActivityMetrics.ZERO.copy(countOpenedPr = 1)
                    "closed" -> ActivityMetrics.ZERO.copy(countClosedPr = 1)
                    else -> ActivityMetrics.ZERO
                }
            }

            "PullRequestReviewEvent", "PullRequestReviewCommentEvent" -> {
                ActivityMetrics.ZERO.copy(countReviewedPr = 1)
            }

            else -> {
                ActivityMetrics.ZERO
            }
        }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GitHubEventResponse(
        val type: String? = null,
        @JsonProperty("created_at") val createdAt: String? = null,
        val payload: GitHubPayloadResponse? = null,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class GitHubPayloadResponse(
        val action: String? = null,
        val size: Int? = null,
        @JsonProperty("distinct_size") val distinctSize: Int? = null,
        val commits: List<Any?>? = null,
    )

    private companion object {
        private const val PER_PAGE = 100
        private const val MAX_PAGES = 3
        val ZONE_MOSCOW: ZoneId = ZoneId.of("Europe/Moscow")
    }
}
