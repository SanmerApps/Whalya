package dev.sanmer.core.response.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageSearched(
    @SerialName("description")
    val description: String,
    @SerialName("is_official")
    val isOfficial: Boolean,
    @SerialName("name")
    val name: String,
    @SerialName("star_count")
    val starCount: Int
)