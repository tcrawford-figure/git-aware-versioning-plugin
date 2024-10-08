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
package io.github.tcrawford.semver.gradle

import io.github.tcrawford.semver.kit.render.Element
import io.github.tcrawford.semver.kit.render.Scribe

fun buildCache(
    fn: BuildCache.Builder.() -> Unit,
): BuildCache {
    val builder = BuildCache.Builder()
    builder.fn()
    return builder.build()
}

class BuildCache(
    private val local: BuildCacheLocal? = null,
) : Element.Block {
    override val name: String = "buildCache"

    override fun render(scribe: Scribe): String = scribe.block(this) { s ->
        local?.render(s)
    }

    class Builder {
        var local: BuildCacheLocal? = null

        fun build(): BuildCache =
            BuildCache(
                local = local,
            )
    }
}
