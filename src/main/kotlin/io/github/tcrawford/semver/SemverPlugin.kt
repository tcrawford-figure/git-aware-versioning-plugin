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
package io.github.tcrawford.semver

import io.github.tcrawford.semver.internal.calculator.VersionFactoryContext
import io.github.tcrawford.semver.internal.calculator.versionFactory
import io.github.tcrawford.semver.internal.extensions.extensions
import io.github.tcrawford.semver.internal.extensions.providers
import io.github.tcrawford.semver.internal.extensions.rootDir
import io.github.tcrawford.semver.internal.logging.registerPostBuildVersionLogMessage
import io.github.tcrawford.semver.internal.properties.BuildMetadataOptions
import io.github.tcrawford.semver.internal.properties.appendBuildMetadata
import io.github.tcrawford.semver.internal.properties.forMajorVersion
import io.github.tcrawford.semver.internal.properties.forTesting
import io.github.tcrawford.semver.internal.properties.modifier
import io.github.tcrawford.semver.internal.properties.overrideVersion
import io.github.tcrawford.semver.internal.properties.stage
import io.github.tcrawford.semver.internal.properties.tagPrefix
import io.github.tcrawford.semver.internal.writer.writeVersionToPropertiesFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.create

class SemverPlugin : Plugin<PluginAware> {
    override fun apply(target: PluginAware) {
        val semverExtension = target.extensions.create<SemverExtension>("semver").apply {
            initialVersion.convention("0.0.0")
            appendBuildMetadata.convention("")
        }

        when (target) {
            is Settings -> {
                target.gradle.settingsEvaluated {
                    val nextVersion = target.calculateVersion(semverExtension)
                    target.gradle.beforeProject {
                        it.version = nextVersion
                    }
                }
            }

            is Project -> {
                target.afterEvaluate {
                    val nextVersion = target.calculateVersion(semverExtension)
                    target.version = nextVersion
                }
            }

            else -> error("Not a project or settings")
        }
    }

    private fun PluginAware.calculateVersion(semverExtension: SemverExtension): String {
        val versionFactoryContext = VersionFactoryContext(
            initialVersion = semverExtension.initialVersion.get(),
            stage = this.stage.get(),
            modifier = this.modifier.get(),
            forTesting = this.forTesting.get(),
            overrideVersion = this.overrideVersion.orNull,
            forMajorVersion = this.forMajorVersion.orNull,
            rootDir = semverExtension.rootProjectDir.getOrElse { this.rootDir }.asFile,
            mainBranch = semverExtension.mainBranch.orNull,
            developmentBranch = semverExtension.developmentBranch.orNull,
            appendBuildMetadata = (appendBuildMetadata.takeIf { it.isPresent } ?: semverExtension.appendBuildMetadata)
                .map { BuildMetadataOptions.from(it, BuildMetadataOptions.NEVER) }
                .get(),
        )

        val nextVersion = this.providers.versionFactory(versionFactoryContext).get()

        this.registerPostBuildVersionLogMessage(nextVersion)
        this.writeVersionToPropertiesFile(nextVersion, tagPrefix.get())

        return nextVersion
    }
}
