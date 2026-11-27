import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "ru.ifmo.se.api"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.spring.boot.starters)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.bundles.jjwt.runtime)
    implementation(libs.aws.s3)
    implementation(libs.uuid.creator)
    runtimeOnly(libs.postgresql)
    developmentOnly(libs.spring.boot.docker.compose)
}

val javaVersion = libs.versions.java.get()

kotlin {
    jvmToolchain(javaVersion.toInt())
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(javaVersion))
    }
}

tasks.test {
    useJUnitPlatform()
}

apply(from = "gradle/ktlint.gradle.kts")
