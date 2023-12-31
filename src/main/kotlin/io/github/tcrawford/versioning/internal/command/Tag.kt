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
