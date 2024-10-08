/*
 * Copyright (C) 2024 Tyler Crawford
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tcrawford.semver.specs

import io.github.tcrawford.semver.kotest.GradleProjectsExtension
import io.github.tcrawford.semver.kotest.shouldOnlyHave
import io.github.tcrawford.semver.projects.RegularProject
import io.github.tcrawford.semver.projects.SettingsProject
import io.github.tcrawford.semver.projects.SubprojectProject
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class MissingLocalBranchSpec : FunSpec({
    val projects = install(
        GradleProjectsExtension(
            RegularProject(projectName = "regular-project"),
            SettingsProject(projectName = "settings-project"),
            SubprojectProject(projectName = "subproject-project"),
        ),
    )

    val mainBranch = "master"
    val developmentBranch = "dev"
    val featureBranch = "feature-3"

    test("on development branch, missing local main branch") {
        // Given
        projects.git {
            initialBranch = mainBranch
            actions = actions {
                commit(message = "1 commit on $mainBranch", tag = "0.2.5")

                checkout(developmentBranch)
                commit(message = "1 commit on $developmentBranch")

                removeLocalBranch(mainBranch)
            }
        }

        // When
        projects.build(GradleVersion.current())

        // Then
        projects.versions shouldOnlyHave "0.2.6-dev.1"
    }

    test("on feature branch, missing local development branch") {
        // Given
        projects.git {
            initialBranch = mainBranch
            debugging = true
            actions = actions {
                commit(message = "1 commit on $mainBranch", tag = "0.2.5")

                checkout(developmentBranch)
                commit(message = "1 commit on $developmentBranch")

                checkout(featureBranch)
                commit(message = "1 commit on $featureBranch")

                removeLocalBranch(developmentBranch)
            }
        }

        // When
        projects.build(GradleVersion.current())

        // Then
        projects.versions shouldOnlyHave "0.2.6-feature-3.1"
    }
})
