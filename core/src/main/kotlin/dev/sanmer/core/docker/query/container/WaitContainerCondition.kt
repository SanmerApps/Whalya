package dev.sanmer.core.docker.query.container

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class WaitContainerCondition {
    @SerialName("not-running")
    NotRunning,

    @SerialName("next-exit")
    NextExit,

    @SerialName("removed")
    Removed;

    override fun toString(): String {
        return when (this) {
            NotRunning -> "not-running"
            NextExit -> "next-exit"
            Removed -> "removed"
        }
    }
}
