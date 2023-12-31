import com.adarshr.gradle.testlogger.theme.ThemeType
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.publish.plugin)

    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.binary.compatibility.validator)

    alias(libs.plugins.test.logger)
    alias(libs.plugins.gradle.testkit)
    idea
}

group = "io.github.tcrawford.gradle"
version = "0.0.7"

val testImplementation: Configuration by configurations.getting

val functionalTestImplementation: Configuration by configurations.getting {
    extendsFrom(testImplementation)
}

sourceSets {
    named("functionalTest") {
        compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
        runtimeClasspath += output + compileClasspath
    }
}

dependencies {
    implementation(gradleKotlinDsl())

    implementation(libs.jgit)
    implementation(libs.kotlin.semver)

    testImplementation(gradleTestKit())
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.datatest)
}

tasks {
    withType<Test>().configureEach {
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

    register("lint") {
        group = "verification"
        description = "Check all code using configured linters. Runs 'ktlintCheck'"
        dependsOn("ktlintCheck")
    }
}

idea {
    module {
        // Marks the functionTest as a test source set
        testSources.from(sourceSets.functionalTest.get().allSource.srcDirs)
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

apiValidation {
    ignoredPackages += listOf(
        // Internal package is not part of the public API
        "io.github.tcrawford.versioning.internal",
    )
}

gradleTestKitSupport {
    withSupportLibrary("0.13")
}

gradlePlugin {
    plugins {
        register("versioning") {
            id = "io.github.tcrawford.versioning"
            displayName = "Git Aware Versioning Plugin"
            description = "Git Aware Versioning Plugin"
            implementationClass = "io.github.tcrawford.versioning.GitAwareVersioningPlugin"
        }
    }
}
