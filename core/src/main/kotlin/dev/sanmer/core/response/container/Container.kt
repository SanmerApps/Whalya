package dev.sanmer.core.response.container

import dev.sanmer.core.request.container.ContainerConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Container(
    @SerialName("Id")
    val id: String,
    @SerialName("Names")
    val names: List<String>,
    @SerialName("Image")
    val image: String,
    @SerialName("ImageID")
    val imageId: String,
    @SerialName("Command")
    val command: String,
    @SerialName("Created")
    val created: Long,
    @SerialName("Ports")
    val ports: List<Port>,
    @SerialName("SizeRw")
    val sizeRw: Long = 0,
    @SerialName("SizeRootFs")
    val sizeRootFs: Long = 0,
    @SerialName("Labels")
    val labels: Map<String, String> = emptyMap(),
    @SerialName("State")
    val state: State,
    @SerialName("Status")
    val status: String,
    @SerialName("HostConfig")
    val hostConfig: HostConfig,
    @SerialName("NetworkSettings")
    val networkSettings: NetworkSettings,
    @SerialName("Mounts")
    val mounts: List<ContainerLowLevel.MountPoint>
) {
    @Serializable
    data class Port(
        @SerialName("IP")
        val ip: String = "",
        @SerialName("PrivatePort")
        val privatePort: Long,
        @SerialName("PublicPort")
        val publicPort: Long = 0,
        @SerialName("Type")
        val type: String
    )

    @Serializable
    enum class State {
        @SerialName("created")
        Created,

        @SerialName("running")
        Running,

        @SerialName("paused")
        Paused,

        @SerialName("restarting")
        Restarting,

        @SerialName("exited")
        Exited,

        @SerialName("removing")
        Removing,

        @SerialName("dead")
        Dead;

        val isRunning inline get() = this == Running
        val isPaused inline get() = this == Paused
        val isExited inline get() = this == Exited
    }

    @Serializable
    data class HostConfig(
        @SerialName("NetworkMode")
        val networkMode: String,
        @SerialName("Annotations")
        val annotations: Map<String, String> = emptyMap()
    )

    @Serializable
    data class NetworkSettings(
        @SerialName("Networks")
        val networks: Map<String, ContainerConfig.Networking.EndpointSettings>
    )
}