package dev.sanmer.core.docker.response.container

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContainerCreated(
    @SerialName("Id") val id: String,
    @SerialName("Warnings") val warnings: List<String>
)