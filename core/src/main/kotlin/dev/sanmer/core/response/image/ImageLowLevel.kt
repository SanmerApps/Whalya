package dev.sanmer.core.response.image

import dev.sanmer.core.response.container.ContainerLowLevel
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageLowLevel(
    @SerialName("Id")
    val id: String,
    @SerialName("RepoTags")
    val repoTags: List<String> = emptyList(),
    @SerialName("RepoDigests")
    val repoDigests: List<String>,
    @SerialName("Parent")
    val parent: String = "",
    @SerialName("Comment")
    val comment: String = "",
    @SerialName("Created")
    val created: Instant = Instant.fromEpochMilliseconds(0),
    @SerialName("DockerVersion")
    val dockerVersion: String = "",
    @SerialName("Author")
    val author: String,
    @SerialName("Config")
    val config: ContainerLowLevel.Config,
    @SerialName("Architecture")
    val architecture: String,
    @SerialName("Variant")
    val variant: String = "",
    @SerialName("Os")
    val os: String,
    @SerialName("OsVersion")
    val osVersion: String = "",
    @SerialName("Size")
    val size: Long,
    @SerialName("GraphDriver")
    val graphDriver: ContainerLowLevel.DriverData,
    @SerialName("RootFS")
    val rootFS: RootFS,
    @SerialName("Metadata")
    val metadata: Metadata
) {
    @Serializable
    data class RootFS(
        @SerialName("Type")
        val type: String,
        @SerialName("Layers")
        val layers: List<String>
    )

    @Serializable
    data class Metadata(
        @SerialName("LastTagTime")
        val lastTagTime: Instant = Instant.fromEpochMilliseconds(0)
    )
}
