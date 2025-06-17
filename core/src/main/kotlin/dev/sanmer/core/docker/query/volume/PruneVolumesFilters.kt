package dev.sanmer.core.docker.query.volume

import dev.sanmer.core.JsonCompat.encodeJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PruneVolumesFilters(
    @SerialName("label") val label: List<String> = emptyList(),
    @SerialName("all") val all: List<String> = emptyList()
) {
    override fun toString(): String {
        return encodeJson(pretty = false)
    }
}