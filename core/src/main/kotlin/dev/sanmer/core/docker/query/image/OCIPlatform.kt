package dev.sanmer.core.docker.query.image

import dev.sanmer.core.JsonCompat.encodeJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OCIPlatform(
    @SerialName("architecture") val architecture: String,
    @SerialName("os") val os: String,
    @SerialName("os.version") val osVersion: String = "",
    @SerialName("os.features") val osFeatures: List<String> = emptyList(),
    @SerialName("variant") val variant: String
) {
    override fun toString(): String {
        return encodeJson(pretty = false)
    }
}