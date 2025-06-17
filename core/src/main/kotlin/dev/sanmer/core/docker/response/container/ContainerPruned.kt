package dev.sanmer.core.docker.response.container

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContainerPruned(
    @SerialName("ContainersDeleted") val containersDeleted: List<String> = emptyList(),
    @SerialName("SpaceReclaimed") val spaceReclaimed: Long
)
