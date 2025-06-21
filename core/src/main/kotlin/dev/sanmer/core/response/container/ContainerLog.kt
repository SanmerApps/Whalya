package dev.sanmer.core.response.container

import io.ktor.http.ContentType
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

    companion object Default {
        internal val RAW = ContentType("application", "vnd.docker.raw-stream")
        internal val MULTIPLEXED = ContentType("application", "vnd.docker.multiplexed-stream")
        internal const val MULTIPLEXED_BLOCK_SIZE = 8
    }
}