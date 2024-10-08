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

import io.github.tcrawford.semver.gradle.semver
import io.github.tcrawford.semver.kotest.GradleProjectsExtension
import io.github.tcrawford.semver.kotest.shouldOnlyHave
import io.github.tcrawford.semver.projects.RegularProject
import io.github.tcrawford.semver.projects.SettingsProject
import io.github.tcrawford.semver.projects.SubprojectProject
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class UseDevelopmentBranchSpec : FunSpec({
    val mainBranch = "main"
    val developBranch = "staging"
    val featureBranch = "feature-1"

    context("should use development branch") {
        val semver = semver {
            this.mainBranch = mainBranch
        }

        val projects = install(
            GradleProjectsExtension(
                RegularProject(projectName = "regular-project", semver = semver),
                SettingsProject(projectName = "settings-project", semver = semver),
                SubprojectProject(projectName = "subproject-project", semver = semver),
            ),
        )

        test("on $developBranch branch") {
            // Given
            projects.git {
                initialBranch = mainBranch
                actions = actions {
                    commit("1 commit on $mainBranch", tag = "1.0.0")
                    checkout(developBranch)
                    commit("1 commit on $developBranch")
                }
            }
            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyHave "1.0.1-$developBranch.1"
        }

        test("on $featureBranch branch") {
            // Given
            projects.git {
                initialBranch = mainBranch
                actions = actions {
                    commit("1 commit on $mainBranch", tag = "1.0.0")
                    checkout(developBranch)
                    commit("1 commit on $developBranch")
                    checkout(featureBranch)
                    commit("1 commit on $featureBranch")
                }
            }
            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyHave "1.0.1-$featureBranch.2"
        }
    }
})
