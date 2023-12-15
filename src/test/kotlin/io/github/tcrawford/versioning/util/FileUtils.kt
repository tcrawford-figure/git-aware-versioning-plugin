package io.github.tcrawford.versioning.util

import java.io.File

const val BUILD_FILE = "build.gradle.kts"
const val SETTINGS_FILE = "settings.gradle.kts"

private fun String.toFile() = File(this)

fun resolveResource(resourcePath: String): File =
    Thread.currentThread().contextClassLoader.getResource(resourcePath)?.file?.toFile()
        ?: throw IllegalArgumentException("Resource not found: $resourcePath")

fun File.copyResource(resourceFile: File, targetFileName: String) {
    val resourceContent = resourceFile.readText()
    File(this, targetFileName).writeText(resourceContent)
}

fun File.appendBuildCacheConfiguration(buildCacheDir: File) {
    val settingsFile = File(this, SETTINGS_FILE)
    val buildCacheConfig = """
        buildCache {
            local {
                directory = "${buildCacheDir.absolutePath}"
            }
        }
    """.trimIndent()

    settingsFile.appendText(buildCacheConfig)
}

fun File.setupProjectDirectory(buildCacheDir: File, resourcesDirectory: File) {
    // Create build and settings files
    copyResource(resourcesDirectory.resolve(BUILD_FILE), BUILD_FILE)
    copyResource(resourcesDirectory.resolve(SETTINGS_FILE), SETTINGS_FILE)

    // Append build cache configuration to settings.gradle.kts
    appendBuildCacheConfiguration(buildCacheDir)
}