package dev.sanmer.core.request.volume

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VolumeConfig(
    @SerialName("Name")
    val name: String,
    @SerialName("Driver")
    val driver: String,
    @SerialName("DriverOpts")
    val driverOpts: Map<String, String>,
    @SerialName("Labels")
    val labels: Map<String, String> = emptyMap(),
)
