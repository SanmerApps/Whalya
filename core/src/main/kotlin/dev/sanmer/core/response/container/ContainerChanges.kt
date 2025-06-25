package dev.sanmer.core.response.container

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContainerChanges(
    @SerialName("Path")
    val path: String,
    @SerialName("Kind")
    @Contextual
    val kind: ChangeType
) {
    enum class ChangeType {
        Modified,
        Added,
        Deleted
    }
}
