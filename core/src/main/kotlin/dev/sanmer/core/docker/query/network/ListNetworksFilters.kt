package dev.sanmer.core.docker.query.network

import dev.sanmer.core.JsonCompat.encodeJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListNetworksFilters(
    @SerialName("dangling") val dangling: List<String> = emptyList(),
    @SerialName("driver") val driver: List<String> = emptyList(),
    @SerialName("id") val id: List<String> = emptyList(),
    @SerialName("label") val label: List<String> = emptyList(),
    @SerialName("name") val name: List<String> = emptyList(),
    @SerialName("scope") val scope: List<String> = emptyList(),
    @SerialName("type") val type: List<String> = emptyList()
) {
    override fun toString(): String {
        return encodeJson(pretty = false)
    }
}