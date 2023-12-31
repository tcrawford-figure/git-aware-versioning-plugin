package io.github.tcrawford.versioning.internal.logging

import org.gradle.api.logging.Logger

private const val LOG_ERROR_PREFIX = "ERROR "
private const val LOG_QUIET_PREFIX = "QUIET "
private const val LOG_WARN_PREFIX = "WARN "
private const val LOG_LIFECYCLE_PREFIX = "> Version: "
private const val LOG_INFO_PREFIX = "INFO "
private const val LOG_DEBUG_PREFIX = "DEBUG "

private fun String.colored(c: String) = "$c$this\u001B[0m"

fun String.darkgray() = this.colored("\u001B[30m")

fun String.red() = this.colored("\u001B[31m")

fun String.green() = this.colored("\u001B[32m")

fun String.yellow() = this.colored("\u001B[33m")

fun String.purple() = this.colored("\u001B[35m")

fun String.lightgray() = this.colored("\u001B[37m")

fun String.bold() = this.colored("\u001B[1m")

fun Logger.error(message: () -> String) =
    this.error(LOG_ERROR_PREFIX.bold() + message().red())

fun Logger.error(throwable: Throwable?, message: () -> String) =
    this.error(LOG_ERROR_PREFIX.bold() + message().red(), throwable)

fun Logger.quiet(message: () -> String) =
    this.quiet(LOG_QUIET_PREFIX.bold() + message().lightgray())

fun Logger.warn(message: () -> String) =
    this.warn(LOG_WARN_PREFIX.bold() + message().yellow())

fun Logger.lifecycle(message: () -> String) =
    this.lifecycle(LOG_LIFECYCLE_PREFIX.bold() + message().purple())

fun Logger.info(message: () -> String) =
    this.info(LOG_INFO_PREFIX.bold() + message().green())

fun Logger.debug(message: () -> String) =
    this.debug(LOG_DEBUG_PREFIX.bold() + message().darkgray())
