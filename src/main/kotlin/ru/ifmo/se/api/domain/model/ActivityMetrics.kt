package ru.ifmo.se.api.domain.model

data class ActivityMetrics(
    val countOpenedPr: Int,
    val countClosedPr: Int,
    val countReviewedPr: Int,
    val countCommits: Int,
) {
    operator fun plus(other: ActivityMetrics): ActivityMetrics =
        ActivityMetrics(
            countOpenedPr = countOpenedPr + other.countOpenedPr,
            countClosedPr = countClosedPr + other.countClosedPr,
            countReviewedPr = countReviewedPr + other.countReviewedPr,
            countCommits = countCommits + other.countCommits,
        )

    companion object {
        val ZERO = ActivityMetrics(0, 0, 0, 0)
    }
}
