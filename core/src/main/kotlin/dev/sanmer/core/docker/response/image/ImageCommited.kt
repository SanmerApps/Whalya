package dev.sanmer.core.docker.response.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageCommited(
    @SerialName("Id") val id: String
)