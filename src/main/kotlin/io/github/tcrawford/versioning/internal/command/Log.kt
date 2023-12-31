package io.github.tcrawford.versioning.internal.command

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

class Log(
    private val git: Git,
) {
    operator fun invoke(): List<RevCommit> =
        git.log().call().toList()
}
