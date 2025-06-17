package dev.sanmer.core.docker.request.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkConfig(
    @SerialName("Name") val name: String,
    @SerialName("Driver") val driver: String,
    @SerialName("Scope") val scope: String,
    @SerialName("Internal") val internal: Boolean,
    @SerialName("Attachable") val attachable: Boolean,
    @SerialName("Ingress") val ingress: Boolean,
    @SerialName("ConfigOnly") val configOnly: Boolean = false,
    @SerialName("ConfigFrom") val configFrom: ConfigReference,
    @SerialName("IPAM") val ipAM: IPAM,
    @SerialName("EnableIPv4") val enableIPv4: Boolean,
    @SerialName("EnableIPv6") val enableIPv6: Boolean,
    @SerialName("Options") val options: Map<String, String> = emptyMap(),
    @SerialName("Labels") val labels: Map<String, String>
) {
    @Serializable
    data class ConfigReference(
        @SerialName("Network") val network: String
    )

    @Serializable
    data class IPAM(
        @SerialName("Driver") val driver: String = "default",
        @SerialName("Config") val config: List<Config> = emptyList(),
        @SerialName("Options") val options: Map<String, String> = emptyMap()
    ) {
        @Serializable
        data class Config(
            @SerialName("Subnet") val subnet: String,
            @SerialName("IPRange") val ipRange: String = "",
            @SerialName("Gateway") val gateway: String,
            @SerialName("AuxiliaryAddresses") val auxiliaryAddresses: Map<String, String> = emptyMap()
        )
    }
}