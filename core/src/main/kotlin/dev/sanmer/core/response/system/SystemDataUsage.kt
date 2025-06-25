package dev.sanmer.core.response.system

import dev.sanmer.core.response.container.Container
import dev.sanmer.core.response.image.Image
import dev.sanmer.core.response.volume.Volume
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class SystemDataUsage(
    @SerialName("LayersSize")
    val layersSize: Long,
    @SerialName("Images")
    val images: List<Image>,
    @SerialName("Containers")
    val containers: List<Container>,
    @SerialName("Volumes")
    val volumes: List<Volume>,
    @SerialName("BuildCache")
    val buildCache: List<BuildCache>
) {
    @Serializable
    data class BuildCache(
        @SerialName("ID")
        val id: String,
        @SerialName("Parents")
        val parents: List<String> = emptyList(),
        @SerialName("Type")
        val type: String,
        @SerialName("Description")
        val description: String,
        @SerialName("InUse")
        val inUse: Boolean,
        @SerialName("Shared")
        val shared: Boolean,
        @SerialName("Size")
        val size: Long,
        @SerialName("CreatedAt")
        @Contextual
        val createdAt: Instant,
        @SerialName("LastUsedAt")
        @Contextual
        val lastUsedAt: Instant = Instant.fromEpochMilliseconds(0),
        @SerialName("UsageCount")
        val usageCount: Int
    )
}
