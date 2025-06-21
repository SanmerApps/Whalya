package dev.sanmer.core.request.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OCIPlatform(
    @SerialName("architecture")
    val architecture: String,
    @SerialName("os")
    val os: String,
    @SerialName("os.version")
    val osVersion: String = "",
    @SerialName("os.features")
    val osFeatures: List<String> = emptyList(),
    @SerialName("variant")
    val variant: String
)