package dev.sanmer.core.docker.response.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    @SerialName("Id") val id: String,
    @SerialName("ParentId") val parentId: String = "",
    @SerialName("RepoTags") val repoTags: List<String> = emptyList(),
    @SerialName("RepoDigests") val repoDigests: List<String>,
    @SerialName("Created") val created: Long,
    @SerialName("Size") val size: Long,
    @SerialName("SharedSize") val sharedSize: Long = -1,
    @SerialName("Labels") val labels: Map<String, String> = emptyMap(),
    @SerialName("Containers") val containers: Int = -1
)