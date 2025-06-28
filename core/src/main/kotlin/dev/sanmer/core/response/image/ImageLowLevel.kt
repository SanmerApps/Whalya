package dev.sanmer.core.response.image

import dev.sanmer.core.request.container.ContainerConfig
import dev.sanmer.core.response.container.ContainerLowLevel
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

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
    @Contextual
    val created: Instant = Instant.fromEpochMilliseconds(0),
    @SerialName("DockerVersion")
    val dockerVersion: String = "",
    @SerialName("Author")
    val author: String,
    @SerialName("Config")
    val config: Config,
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
    data class Config(
        @SerialName("User")
        val user: String = "",
        @SerialName("ExposedPorts")
        val exposedPorts: Map<String, ContainerConfig.ExposedPortValue> = emptyMap(),
        @SerialName("Env")
        val env: List<String> = emptyList(),
        @SerialName("Cmd")
        val cmd: List<String> = emptyList(),
        @SerialName("Healthcheck")
        val healthCheck: ContainerConfig.Health = ContainerConfig.Health(),
        @SerialName("ArgsEscaped")
        val argsEscaped: Boolean = false,
        @SerialName("Volumes")
        val volumes: Map<String, ContainerConfig.VolumeValue> = emptyMap(),
        @SerialName("WorkingDir")
        val workingDir: String = "",
        @SerialName("Entrypoint")
        val entryPoint: List<String> = emptyList(),
        @SerialName("OnBuild")
        val onBuild: List<String> = emptyList(),
        @SerialName("Labels")
        val labels: Map<String, String> = emptyMap(),
        @SerialName("StopSignal")
        val stopSignal: String = "",
        @SerialName("Shell")
        val shell: List<String> = emptyList()
    )

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
        @Contextual
        val lastTagTime: Instant = Instant.fromEpochMilliseconds(0)
    )
}
