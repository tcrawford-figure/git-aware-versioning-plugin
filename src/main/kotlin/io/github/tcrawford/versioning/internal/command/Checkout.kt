package io.github.tcrawford.versioning.internal.command

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref

class Checkout(
    private val git: Git,
) {
    operator fun invoke(branchName: String, createBranch: Boolean = false): Ref =
        git.checkout()
            .setName(branchName)
            .setCreateBranch(createBranch)
            .call()
}
