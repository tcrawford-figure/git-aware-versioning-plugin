package io.github.tcrawford.versioning.internal.command

import org.eclipse.jgit.api.Git
import java.io.File

object Init {
    operator fun invoke(
        directory: File,
        bare: Boolean = false,
        initialBranch: String = "main",
    ): Git = Git.init()
        .setDirectory(directory)
        .setBare(bare)
        .setInitialBranch(initialBranch)
        .call()
}
