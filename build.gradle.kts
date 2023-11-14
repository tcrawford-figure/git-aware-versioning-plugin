import com.adarshr.gradle.testlogger.theme.ThemeType
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.test.logger)
    `java-gradle-plugin`
}

group = "com.figure.gradle.semver"
version = "0.0.6"

dependencies {
    implementation(gradleKotlinDsl())

    implementation(libs.jgit)
    implementation(libs.kotlin.semver)
    implementation(libs.kotlin.result)

    testImplementation(gradleTestKit())
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.datatest)
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            setExceptionFormat("full")
            setEvents(listOf("skipped", "failed", "standardOut", "standardError"))
        }
    }

    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }

    check {
        dependsOn("detekt")
    }

    withType<Detekt>().configureEach {
        reports {
            txt.required.set(true)
            html.required.set(true)
            xml.required.set(false)
            sarif.required.set(false)
        }
    }

    register("fmt") {
        group = "verification"
        description = "Format all code using configured formatters. Runs 'ktlintFormat'"
        dependsOn("ktlintFormat")
    }
}

kotlin {
    jvmToolchain(11)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.from(files("detekt.yml"))
}

testlogger {
    theme = ThemeType.STANDARD
    showCauses = true
    slowThreshold = 1000
    showSummary = true
    showStandardStreams = false
}

gradlePlugin {
    plugins {
        create(project.name) {
            id = "com.figure.gradle.settings.semver"
            displayName = "Semver Settings Plugin"
            description = "Semver Settings Plugin"
            implementationClass = "com.figure.gradle.semver.SemverSettingsPlugin"
        }
    }
}
