package dev.sanmer.core.docker.query.image

import dev.sanmer.core.JsonCompat.encodeJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PruneBuilderCachesFilters(
    @SerialName("until") val until: List<String> = emptyList(),
    @SerialName("id") val id: List<String> = emptyList(),
    @SerialName("parent") val parent: List<String> = emptyList(),
    @SerialName("type") val type: List<String> = emptyList(),
    @SerialName("description") val description: List<String> = emptyList()
) {
    override fun toString(): String {
        return encodeJson(pretty = false)
    }
}
