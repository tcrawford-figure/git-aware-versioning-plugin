package io.github.tcrawford.versioning.integration

import io.github.tcrawford.versioning.testkit.GradleProjectListener
import io.github.tcrawford.versioning.testkit.repositoryConfig
import io.github.tcrawford.versioning.util.GradleArgs
import io.github.tcrawford.versioning.util.resolveResource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CalculateNextVersionWithoutInputsSpec : FunSpec({
    val gradleSettingsProjectListener = GradleProjectListener(resolveResource("settings-sample"))

    val defaultArguments = listOf(
        GradleArgs.STACKTRACE,
        GradleArgs.PARALLEL,
        GradleArgs.BUILD_CACHE,
        GradleArgs.CONFIGURATION_CACHE,
        GradleArgs.semverForTesting(true),
    )

    listener(gradleSettingsProjectListener)

    context("should calculate next version without inputs") {
        val mainBranch = "main"
        val developmentBranch = "develop"
        val featureBranch = "myname/sc-123456/my-awesome-feature"

        test("on main branch") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(mainBranch)
                }
            }

            // When
            val runner = gradleSettingsProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            runner.build()

            // Then
            gradleSettingsProjectListener.computedVersion shouldBe "1.0.1"
        }

        test("on development branch") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")
                }
            }

            // When
            val runner = gradleSettingsProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            runner.build()

            // Then
            gradleSettingsProjectListener.computedVersion shouldBe "1.0.1-develop.1"
        }

        test("on development branch with latest development tag") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch", tag = "1.0.1-develop.1")
                    commit(message = "2 commit on $developmentBranch")
                    commit(message = "3 commit on $developmentBranch")
                }
            }

            // When
            val runner = gradleSettingsProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            runner.build()

            // Then
            gradleSettingsProjectListener.computedVersion shouldBe "1.0.1-develop.3"
        }

        test("on feature branch off development branch") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(featureBranch)
                    commit(message = "1 commit on $developmentBranch")
                    commit(message = "2 commit on $developmentBranch")
                }
            }

            // When
            val runner = gradleSettingsProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            runner.build()

            // Then
            gradleSettingsProjectListener.computedVersion shouldBe "1.0.1-myname-sc-123456-my-awesome-feature.2"
        }

        test("on feature branch off main branch") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(featureBranch)
                    commit(message = "1 commit on $developmentBranch")
                    commit(message = "2 commit on $developmentBranch")
                }
            }

            // When
            val runner = gradleSettingsProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            runner.build()

            // Then
            gradleSettingsProjectListener.computedVersion shouldBe "1.0.1-myname-sc-123456-my-awesome-feature.2"
        }

        test("for develop branch after committing to feature branch and switching back to develop") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")

                    checkout(developmentBranch)
                }
            }

            // When
            val runner = gradleSettingsProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            runner.build()

            // Then
            gradleSettingsProjectListener.computedVersion shouldBe "1.0.1-develop.1"
        }

        test("next branch version where last tag is prerelease") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.3-alpha.1")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            // When
            val runner = gradleSettingsProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            runner.build()

            // Then
            gradleSettingsProjectListener.computedVersion shouldBe "1.0.3-myname-sc-123456-my-awesome-feature.1"
        }
    }
})