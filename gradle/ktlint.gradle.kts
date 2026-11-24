val ktlint = configurations.create("ktlint")

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    ktlint(variantOf(libs.findLibrary("ktlint-cli").get()) { classifier("all") })
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
