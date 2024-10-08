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
package io.github.tcrawford.semver.internal.writer

import io.github.tcrawford.semver.Constants
import io.github.tcrawford.semver.internal.extensions.getOrCreate
import io.github.tcrawford.semver.internal.extensions.projectDir
import org.gradle.api.plugins.PluginAware

fun PluginAware.writeVersionToPropertiesFile(version: String, tagPrefix: String) {
    projectDir.resolve(Constants.SEMVER_PROPERTY_PATH).getOrCreate().writeText(
        """
        |version=$version
        |versionTag=$tagPrefix$version
        """.trimMargin(),
    )
}
