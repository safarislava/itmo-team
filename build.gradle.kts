plugins {
    kotlin("jvm") version "2.4.20"
    kotlin("plugin.spring") version "2.4.20"
    kotlin("plugin.jpa") version "2.4.21"
    kotlin("kapt") version "2.4.0"
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.ifmo.se.api"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.mapstruct:mapstruct:1.6.3")
    kapt("org.mapstruct:mapstruct-processor:1.6.3")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
        arg("mapstruct.unmappedTargetPolicy", "IGNORE")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

tasks.test {
    useJUnitPlatform()
}

apply(from = "gradle/ktlint.gradle.kts")
