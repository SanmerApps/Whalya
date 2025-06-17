package dev.sanmer.core.docker.response.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BuilderCachePruned(
    @SerialName("CachesDeleted") val cachesDeleted: List<String> = emptyList(),
    @SerialName("SpaceReclaimed") val spaceReclaimed: Long
)