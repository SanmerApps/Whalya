package dev.sanmer.core.docker.response.volume

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VolumePruned(
    @SerialName("VolumesDeleted") val volumesDeleted: List<String>,
    @SerialName("SpaceReclaimed") val spaceReclaimed: Long
)
