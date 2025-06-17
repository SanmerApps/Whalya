package dev.sanmer.core.docker.response.container

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
        internal const val RAW = "application/vnd.docker.raw-stream"
        internal const val MULTIPLEXED = "application/vnd.docker.multiplexed-stream"
        internal const val MULTIPLEXED_BLOCK_SIZE = 8
    }
}