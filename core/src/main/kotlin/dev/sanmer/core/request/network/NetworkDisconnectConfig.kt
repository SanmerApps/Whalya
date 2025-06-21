package dev.sanmer.core.request.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkDisconnectConfig(
    @SerialName("Container")
    val container: String,
    @SerialName("Force")
    val force: Boolean
)
