val ktlint = configurations.create("ktlint")

dependencies {
    ktlint("com.pinterest.ktlint:ktlint-cli:1.8.0:all")
}

val ktlintCheck =
    tasks.register<JavaExec>("ktlintCheck") {
        group = "verification"
        description = "Check Kotlin code style."
        classpath = ktlint
        mainClass.set("com.pinterest.ktlint.Main")
        jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
        args(
            "src/**/*.kt",
            "*.kts",
            "!**/build/**",
        )
    }

val ktlintFormat =
    tasks.register<JavaExec>("ktlintFormat") {
        group = "formatting"
        description = "Fix Kotlin code style deviations."
        classpath = ktlint
        mainClass.set("com.pinterest.ktlint.Main")
        jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
        args(
            "-F",
            "src/**/*.kt",
            "*.kts",
            "!**/build/**",
        )
    }

tasks.named("check") {
    dependsOn(ktlintCheck)
}
