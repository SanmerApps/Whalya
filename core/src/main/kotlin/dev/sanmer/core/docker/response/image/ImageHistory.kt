package dev.sanmer.core.docker.response.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageHistory(
    @SerialName("Id") val id: String,
    @SerialName("Created") val created: Long,
    @SerialName("CreatedBy") val createdBy: String,
    @SerialName("Tags") val tags: List<String> = emptyList(),
    @SerialName("Size") val size: Long,
    @SerialName("Comment") val comment: String
)