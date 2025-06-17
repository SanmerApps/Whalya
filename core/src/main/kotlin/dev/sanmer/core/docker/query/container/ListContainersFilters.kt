package dev.sanmer.core.docker.query.container

import dev.sanmer.core.JsonCompat.encodeJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListContainersFilters(
    @SerialName("ancestor") val ancestor: List<String> = emptyList(),
    @SerialName("before") val before: List<String> = emptyList(),
    @SerialName("expose") val expose: List<String> = emptyList(),
    @SerialName("exited") val exited: List<String> = emptyList(),
    @SerialName("health") val health: List<String> = emptyList(),
    @SerialName("id") val id: List<String> = emptyList(),
    @SerialName("isolation") val isolation: List<String> = emptyList(),
    @SerialName("is-task") val isTask: List<String> = emptyList(),
    @SerialName("label") val label: List<String> = emptyList(),
    @SerialName("name") val name: List<String> = emptyList(),
    @SerialName("network") val network: List<String> = emptyList(),
    @SerialName("publish") val publish: List<String> = emptyList(),
    @SerialName("since") val since: List<String> = emptyList(),
    @SerialName("status") val status: List<String> = emptyList(),
    @SerialName("volume") val volume: List<String> = emptyList()
) {
    override fun toString(): String {
        return encodeJson(pretty = false)
    }
}