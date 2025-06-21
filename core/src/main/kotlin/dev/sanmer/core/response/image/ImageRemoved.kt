package dev.sanmer.core.response.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageRemoved(
    @SerialName("Untagged")
    val untagged: String = "",
    @SerialName("Deleted")
    val deleted: String = ""
)