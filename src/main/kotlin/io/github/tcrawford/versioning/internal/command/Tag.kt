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
package io.github.tcrawford.versioning.internal.command

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref

class Tag(
    private val git: Git,
) {
    operator fun invoke(tagName: String): Ref? =
        git.tag()
            .setName(tagName)
            .call()

    fun delete(vararg tag: String): MutableList<String>? =
        git.tagDelete()
            .setTags(*tag)
            .call()
}
