package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.git.Script
import io.github.tcrawford.versioning.internal.command.GitState
import io.github.tcrawford.versioning.kotest.GradleProjectsExtension
import io.github.tcrawford.versioning.kotest.shouldOnlyHave
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import org.gradle.util.GradleVersion

class CalculateNextVersionInNonNominalStateSpec : FunSpec({
    val projects = install(
        GradleProjectsExtension(
            RegularProject(projectName = "regular-project"),
            SettingsProject(projectName = "settings-project"),
            SubprojectProject(projectName = "subproject-project"),
        ),
    )

    val mainBranch = "main"
    val featureBranch = "feature-branch"

    context("should calculate next version in non nominal state when") {
        withData(
            nameFn = { "running ${it.script.scriptFileName}" },
            TestData(Script.CREATE_BISECTING_STATE, "0.2.6-${GitState.BISECTING.description}"),
            TestData(Script.CREATE_CHERRY_PICKING_STATE, "0.2.6-${GitState.CHERRY_PICKING.description}"),
            TestData(Script.CREATE_MERGING_STATE, "0.2.6-${GitState.MERGING.description}"),
            TestData(Script.CREATE_REBASING_STATE, "0.2.6-${GitState.REBASING.description}"),
            TestData(Script.CREATE_REVERTING_STATE, "0.2.6-${GitState.REVERTING.description}"),
            TestData(Script.CREATE_DETACHED_HEAD_STATE, "0.2.6-${GitState.DETACHED_HEAD.description}"),
        ) {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")
                    runScript(it.script, mainBranch, featureBranch)
                }
            }

            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyHave it.expectedVersion
        }
    }
})

private data class TestData(
    val script: Script,
    val expectedVersion: String,
)
