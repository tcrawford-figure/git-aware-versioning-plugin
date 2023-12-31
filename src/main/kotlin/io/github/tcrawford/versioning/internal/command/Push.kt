package io.github.tcrawford.versioning.internal.command

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.transport.PushResult
import org.eclipse.jgit.transport.RefSpec

class Push(
    private val git: Git,
) {
    operator fun invoke(): MutableIterable<PushResult>? =
        git.push().call()

    fun branch(branch: String): MutableIterable<PushResult>? =
        git.push()
            .setRefSpecs(RefSpec("${Constants.R_HEADS}$branch:${Constants.R_HEADS}$branch"))
            .call()

    fun tag(tag: String): MutableIterable<PushResult>? =
        git.push()
            .setRefSpecs(RefSpec("${Constants.R_TAGS}$tag:${Constants.R_TAGS}$$tag"))
            .call()

    fun allBranches(): MutableIterable<PushResult>? =
        git.push()
            .setPushAll() // Push all branches under refs/heads/*
            .call()

    fun allTags(): MutableIterable<PushResult>? =
        git.push()
            .setPushTags() // Push all tags under refs/tags/*
            .call()

    fun all(): MutableIterable<PushResult>? =
        git.push()
            .setPushAll() // Push all branches under refs/heads/*
            .setPushTags() // Push all tags under refs/tags/*
            .call()
}
