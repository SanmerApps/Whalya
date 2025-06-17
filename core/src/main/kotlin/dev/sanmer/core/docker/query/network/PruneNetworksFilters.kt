package dev.sanmer.core.docker.query.network

import dev.sanmer.core.JsonCompat.encodeJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PruneNetworksFilters(
    @SerialName("until") val until: List<String> = emptyList(),
    @SerialName("label") val label: List<String> = emptyList()
) {
    override fun toString(): String {
        return encodeJson(pretty = false)
    }
}