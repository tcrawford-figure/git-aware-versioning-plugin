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
package io.github.tcrawford.semver.git

import io.github.tcrawford.semver.projects.AbstractProject

fun gitInstance(project: AbstractProject, block: GitInstance.Builder.() -> Unit): GitInstance =
    GitInstance.Builder(project).apply(block).build()

data class GitInstance(
    val project: AbstractProject,
    val debugging: Boolean,
    val initialBranch: String,
    val actions: GitActionsConfig,
) {
    class Builder(
        private val project: AbstractProject,
    ) {
        var debugging: Boolean = false
        var initialBranch: String = "main"
        var actions: GitActionsConfig = GitActionsConfig(project)

        fun actions(config: Actions.() -> Unit): GitActionsConfig {
            actions.actions(config)
            return actions
        }

        fun build() = GitInstance(
            project = project,
            debugging = debugging,
            initialBranch = initialBranch,
            actions = actions,
        )
    }
}
