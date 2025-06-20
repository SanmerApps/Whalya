package dev.sanmer.core.response.container

import dev.sanmer.core.serializer.ChangeTypeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContainerChanges(
    @SerialName("Path")
    val path: String,
    @SerialName("Kind")
    val kind: ChangeType
) {
    @Serializable(with = ChangeTypeSerializer::class)
    enum class ChangeType {
        Modified,
        Added,
        Deleted
    }
}
