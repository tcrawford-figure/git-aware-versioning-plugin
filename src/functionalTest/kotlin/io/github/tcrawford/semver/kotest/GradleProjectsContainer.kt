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
package io.github.tcrawford.semver.kotest

import io.github.tcrawford.semver.projects.AbstractProject
import io.github.tcrawford.semver.projects.GradleProjects
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

class GradleProjectsExtension(
    private vararg val abstractProjects: AbstractProject,
) : MountableExtension<Unit, GradleProjects>, AfterSpecListener, AfterTestListener {
    private lateinit var projects: GradleProjects

    override fun mount(configure: Unit.() -> Unit): GradleProjects {
        return GradleProjects.gradleProjects(projects = abstractProjects).also { projects = it }
    }

    override suspend fun afterSpec(spec: Spec) {
        projects.close()
    }

    override suspend fun afterAny(testCase: TestCase, result: TestResult) {
        projects.cleanAfterAny()
    }
}