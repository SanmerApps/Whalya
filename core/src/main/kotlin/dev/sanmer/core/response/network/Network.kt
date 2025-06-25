package dev.sanmer.core.response.network

import dev.sanmer.core.request.network.NetworkConfig
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Network(
    @SerialName("Name")
    val name: String,
    @SerialName("Id")
    val id: String,
    @SerialName("Created")
    @Contextual
    val created: Instant,
    @SerialName("Scope")
    val scope: String,
    @SerialName("Driver")
    val driver: String,
    @SerialName("EnableIPv4")
    val enableIPv4: Boolean,
    @SerialName("EnableIPv6")
    val enableIPv6: Boolean,
    @SerialName("IPAM")
    val ipAM: NetworkConfig.IPAM,
    @SerialName("Internal")
    val internal: Boolean = false,
    @SerialName("Attachable")
    val attachable: Boolean = false,
    @SerialName("Ingress")
    val ingress: Boolean = false,
    @SerialName("ConfigFrom")
    val configFrom: NetworkConfig.ConfigReference,
    @SerialName("ConfigOnly")
    val configOnly: Boolean = false,
    @SerialName("Containers")
    val containers: Map<String, NetworkContainer>,
    @SerialName("Options")
    val options: Map<String, String> = emptyMap(),
    @SerialName("Labels")
    val labels: Map<String, String> = emptyMap(),
    @SerialName("Peers")
    val peers: List<PeerInfo> = emptyList()
) {
    @Serializable
    data class NetworkContainer(
        @SerialName("Name")
        val name: String,
        @SerialName("EndpointID")
        val endpointID: String,
        @SerialName("MacAddress")
        val macAddress: String,
        @SerialName("IPv4Address")
        val ipv4Address: String,
        @SerialName("IPv6Address")
        val ipv6Address: String
    )

    @Serializable
    data class PeerInfo(
        @SerialName("Name")
        val name: String,
        @SerialName("IP")
        val ip: String
    )
}
