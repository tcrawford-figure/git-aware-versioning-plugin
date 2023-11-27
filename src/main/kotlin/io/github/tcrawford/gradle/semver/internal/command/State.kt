package io.github.tcrawford.gradle.semver.internal.command

import org.eclipse.jgit.api.Git

// This is heavily influenced by the gitstatus logic found here:
// https://github.com/magicmonty/bash-git-prompt/
// TODO: Future consideration: Support rebasing step in the version. So something like `1.2.3-rebasing.2-12`
//  would mean step 2 out of 12 of rebasing.
internal class State(
    private val git: Git
) {
    operator fun invoke(): GitState = when {
        rebasing -> GitState.REBASING
        merging -> GitState.MERGING
        cherryPicking -> GitState.CHERRY_PICKING
        reverting -> GitState.REVERTING
        bisecting -> GitState.BISECTING
        else -> GitState.NOMINAL
    }

    private val rebasing: Boolean
        get() = git.repository.directory.resolve("rebase-merge").exists()

    private val merging: Boolean
        get() = git.repository.directory.resolve("MERGE_HEAD").exists()

    private val cherryPicking: Boolean
        get() = git.repository.directory.resolve("CHERRY_PICK_HEAD").exists()

    private val reverting: Boolean
        get() = git.repository.directory.resolve("REVERT_HEAD").exists()

    private val bisecting: Boolean
        get() = git.repository.directory.resolve("BISECT_LOG").exists()
}

internal enum class GitState(val description: String) {
    NOMINAL(""),
    REBASING("rebasing"),
    MERGING("merging"),
    CHERRY_PICKING("cherry-picking"),
    REVERTING("reverting"),
    BISECTING("bisecting")
}