package io.github.tcrawford.gradle.semver.internal.calculator

import io.github.tcrawford.gradle.semver.internal.Modifier
import io.github.tcrawford.gradle.semver.internal.Stage

internal data class VersionCalculatorContext(
    val stage: Stage,
    val modifier: Modifier,
    val forTesting: Boolean,
    val mainBranch: String? = null,
    val developmentBranch: String? = null,
)