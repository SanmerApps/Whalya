package dev.sanmer.core.docker.query.image

import dev.sanmer.core.JsonCompat.encodeJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchImagesFilters(
    @SerialName("is-official") val isOfficial: List<String> = emptyList(),
    @SerialName("stars") val stars: List<String> = emptyList()
) {
    override fun toString(): String {
        return encodeJson(pretty = false)
    }
}