package dev.sanmer.core.response.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkPruned(
    @SerialName("NetworksDeleted")
    val networksDeleted: List<String> = emptyList()
)