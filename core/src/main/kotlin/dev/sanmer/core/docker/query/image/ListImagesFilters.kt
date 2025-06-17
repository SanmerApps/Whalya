package dev.sanmer.core.docker.query.image

import dev.sanmer.core.JsonCompat.encodeJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListImagesFilters(
    @SerialName("before") val before: List<String> = emptyList(),
    @SerialName("dangling") val dangling: List<String> = emptyList(),
    @SerialName("label") val label: List<String> = emptyList(),
    @SerialName("reference") val reference: List<String> = emptyList(),
    @SerialName("since") val since: List<String> = emptyList(),
    @SerialName("until") val until: List<String> = emptyList()
) {
    override fun toString(): String {
        return encodeJson(pretty = false)
    }
}
