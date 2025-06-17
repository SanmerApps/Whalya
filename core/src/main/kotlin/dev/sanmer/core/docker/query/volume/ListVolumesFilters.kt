package dev.sanmer.core.docker.query.volume

import dev.sanmer.core.JsonCompat.encodeJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListVolumesFilters(
    @SerialName("dangling") val dangling: List<String> = emptyList(),
    @SerialName("driver") val driver: List<String> = emptyList(),
    @SerialName("label") val label: List<String> = emptyList(),
    @SerialName("name") val name: List<String> = emptyList()
) {
    override fun toString(): String {
        return encodeJson(pretty = false)
    }
}
