package io.github.tcrawford.gradle.semver.internal.calculator

import io.github.tcrawford.gradle.semver.internal.Modifier
import io.github.tcrawford.gradle.semver.internal.Stage
import io.github.tcrawford.gradle.semver.internal.command.KGit
import io.github.tcrawford.gradle.semver.internal.extensions.sanitizedWithoutPrefix
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.inc

/**
 * Should calculate the next version based on the current branch name
 */
internal class BranchBasedVersionCalculator(
    private val kGit: KGit
) : VersionCalculator {
    override fun calculate(latestVersion: Version, stage: Stage, modifier: Modifier, forTesting: Boolean): String {
        val currentBranch = kGit.branch.currentRef(forTesting)
        val developmentBranch = kGit.branches.developmentBranch

        val prereleaseLabel = currentBranch.sanitizedWithoutPrefix()
        val commitCount = kGit.branches.commitCountBetween(developmentBranch.name, currentBranch.name)
        val prereleaseLabelWithCommitCount = "$prereleaseLabel.$commitCount"

        return latestVersion.inc(modifier.toInc(), prereleaseLabelWithCommitCount).toString()
    }
}