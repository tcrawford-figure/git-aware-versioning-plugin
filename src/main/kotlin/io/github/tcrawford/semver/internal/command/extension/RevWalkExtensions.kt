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
package io.github.tcrawford.semver.internal.command.extension

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk

fun <R> Repository.revWalk(action: (RevWalk) -> R) =
    RevWalk(this).use(action)

fun <R> Git.revWalk(action: (RevWalk) -> R) =
    repository.revWalk(action)
