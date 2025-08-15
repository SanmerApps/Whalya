package dev.sanmer.core.response.container

import kotlinx.serialization.Serializable

@Serializable
data class ContainerLog(
    val type: Type,
    val content: String
) {
    enum class Type {
        Stdin,
        Stdout,
        Stderr;

        val isStdout inline get() = this == Stdout
        val isStderr inline get() = this == Stderr
    }
}