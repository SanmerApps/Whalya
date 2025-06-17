package dev.sanmer.core.docker.response.container

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContainerTop(
    @SerialName("Titles") val titles: List<String>,
    @SerialName("Processes") val processes: List<List<String>>
)
