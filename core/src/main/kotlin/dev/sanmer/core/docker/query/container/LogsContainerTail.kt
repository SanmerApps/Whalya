package dev.sanmer.core.docker.query.container

sealed class LogsContainerTail {
    data object All : LogsContainerTail() {
        override fun toString(): String {
            return "all"
        }
    }

    data class Number(val value: Int) : LogsContainerTail() {
        override fun toString(): String {
            return value.toString()
        }
    }
}