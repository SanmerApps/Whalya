package dev.sanmer.core.response.volume

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VolumeList(
    @SerialName("Volumes")
    val volumes: List<Volume>,
    @SerialName("Warnings")
    val warnings: List<String> = emptyList()
)
