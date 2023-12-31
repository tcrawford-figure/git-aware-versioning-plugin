package io.github.tcrawford.versioning.internal.extensions

import org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME
import org.eclipse.jgit.lib.Constants.R_HEADS
import org.eclipse.jgit.lib.Constants.R_REMOTES
import org.eclipse.jgit.lib.Ref

const val R_REMOTES_ORIGIN = "$R_REMOTES$DEFAULT_REMOTE_NAME"

private val validCharacters: Regex = """[^0-9A-Za-z\-_.]+""".toRegex()

fun Ref.sanitizedWithoutPrefix(): String =
    name.trim()
        .lowercase()
        .replace(R_HEADS, "")
        .replace("$R_REMOTES_ORIGIN/", "")
        .removePrefix("/")
        .replace(validCharacters, "-")
