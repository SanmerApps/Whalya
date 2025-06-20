package dev.sanmer.core.response.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImagePruned(
    @SerialName("ImagesDeleted")
    val imagesDeleted: List<ImageRemoved> = emptyList(),
    @SerialName("SpaceReclaimed")
    val spaceReclaimed: Long
)