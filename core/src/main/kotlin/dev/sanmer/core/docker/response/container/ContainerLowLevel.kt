package dev.sanmer.core.docker.response.container

import dev.sanmer.core.docker.request.container.ContainerConfig
import dev.sanmer.core.docker.request.container.HostConfig
import dev.sanmer.core.docker.serializer.PortMapSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContainerLowLevel(
    @SerialName("Id") val id: String,
    @SerialName("Created") val created: Instant = Instant.fromEpochMilliseconds(0),
    @SerialName("Path") val path: String,
    @SerialName("Args") val args: List<String>,
    @SerialName("State") val state: State = State(),
    @SerialName("Image") val image: String,
    @SerialName("ResolvConfPath") val resolvConfPath: String,
    @SerialName("HostnamePath") val hostnamePath: String,
    @SerialName("HostsPath") val hostsPath: String,
    @SerialName("LogPath") val logPath: String = "",
    @SerialName("Name") val name: String,
    @SerialName("RestartCount") val restartCount: Int,
    @SerialName("Driver") val driver: String,
    @SerialName("Platform") val platform: String,
    @SerialName("MountLabel") val mountLabel: String,
    @SerialName("ProcessLabel") val processLabel: String,
    @SerialName("AppArmorProfile") val appArmorProfile: String,
    @SerialName("ExecIDs") val execIDs: List<String> = emptyList(),
    @SerialName("HostConfig") val hostConfig: HostConfig,
    @SerialName("GraphDriver") val graphDriver: DriverData,
    @SerialName("SizeRw") val sizeRw: Long = 0,
    @SerialName("SizeRootFs") val sizeRootFs: Long = 0,
    @SerialName("Mounts") val mounts: List<MountPoint>,
    @SerialName("Config") val config: Config,
    @SerialName("NetworkSettings") val networkSettings: NetworkSettings
) {
    @Serializable
    data class State(
        @SerialName("Status") val status: Container.State = Container.State.Dead,
        @SerialName("Running") val running: Boolean = false,
        @SerialName("Paused") val paused: Boolean = false,
        @SerialName("Restarting") val restarting: Boolean = false,
        @SerialName("OOMKilled") val ooMKilled: Boolean = true,
        @SerialName("Dead") val dead: Boolean = true,
        @SerialName("Pid") val pid: Int = 0,
        @SerialName("ExitCode") val exitCode: Int = 0,
        @SerialName("Error") val error: String = "",
        @SerialName("StartedAt") val startedAt: Instant = Instant.fromEpochMilliseconds(0),
        @SerialName("FinishedAt") val finishedAt: Instant = Instant.fromEpochMilliseconds(0),
        @SerialName("Health") val health: Health = Health()
    ) {
        @Serializable
        data class Health(
            @SerialName("Status") val status: String = "",
            @SerialName("FailingStreak") val failingStreak: Int = 0,
            @SerialName("Log") val log: List<CheckResult> = emptyList()
        ) {
            @Serializable
            data class CheckResult(
                @SerialName("Start") val start: Instant,
                @SerialName("End") val end: Instant,
                @SerialName("ExitCode") val exitCode: Int,
                @SerialName("Output") val output: String
            )
        }
    }

    @Serializable
    data class DriverData(
        @SerialName("Name") val name: String,
        @SerialName("Data") val data: Map<String, String>
    )

    @Serializable
    data class Config(
        @SerialName("Hostname") val hostname: String = "",
        @SerialName("Domainname") val domainName: String = "",
        @SerialName("User") val user: String = "",
        @SerialName("AttachStdin") val attachStdin: Boolean = false,
        @SerialName("AttachStdout") val attachStdout: Boolean = true,
        @SerialName("AttachStderr") val attachStderr: Boolean = true,
        @SerialName("ExposedPorts") val exposedPorts: Map<String, ContainerConfig.ExposedPortValue> = emptyMap(),
        @SerialName("Tty") val tty: Boolean = false,
        @SerialName("OpenStdin") val openStdin: Boolean = false,
        @SerialName("StdinOnce") val stdinOnce: Boolean = false,
        @SerialName("Env") val env: List<String> = emptyList(),
        @SerialName("Cmd") val cmd: List<String> = emptyList(),
        @SerialName("Healthcheck") val healthCheck: ContainerConfig.Health = ContainerConfig.Health(),
        @SerialName("ArgsEscaped") val argsEscaped: Boolean = false,
        @SerialName("Image") val image: String = "",
        @SerialName("Volumes") val volumes: Map<String, ContainerConfig.VolumeValue> = emptyMap(),
        @SerialName("WorkingDir") val workingDir: String = "",
        @SerialName("Entrypoint") val entryPoint: List<String> = emptyList(),
        @SerialName("NetworkDisabled") val networkDisabled: Boolean = false,
        @SerialName("OnBuild") val onBuild: List<String> = emptyList(),
        @SerialName("Labels") val labels: Map<String, String> = emptyMap(),
        @SerialName("StopSignal") val stopSignal: String = "",
        @SerialName("StopTimeout") val stopTimeout: Int = 10,
        @SerialName("Shell") val shell: List<String> = emptyList()
    )

    @Serializable
    data class MountPoint(
        @SerialName("Type") val type: HostConfig.Mount.Type,
        @SerialName("Name") val name: String = "",
        @SerialName("Source") val source: String,
        @SerialName("Destination") val destination: String,
        @SerialName("Driver") val driver: String = "",
        @SerialName("Mode") val mode: String,
        @SerialName("RW") val rw: Boolean,
        @SerialName("Propagation") val propagation: String
    )

    @Serializable
    data class NetworkSettings(
        @SerialName("Bridge") val bridge: String,
        @SerialName("SandboxID") val sandboxId: String,
        @SerialName("HairpinMode") val hairpinMode: Boolean,
        @SerialName("LinkLocalIPv6Address") val linkLocalIPv6Address: String,
        @SerialName("LinkLocalIPv6PrefixLen") val linkLocalIPv6PrefixLen: Int,
        @SerialName("Ports") val ports: PortMap = PortMap(),
        @SerialName("SandboxKey") val sandboxKey: String,
        @SerialName("SecondaryIPAddresses") val secondaryIPAddresses: List<Address> = emptyList(),
        @SerialName("SecondaryIPv6Addresses") val secondaryIPv6Addresses: List<Address> = emptyList(),
        @SerialName("EndpointID") val endpointId: String,
        @SerialName("Gateway") val gateway: String,
        @SerialName("GlobalIPv6Address") val globalIPv6Address: String,
        @SerialName("GlobalIPv6PrefixLen") val globalIPv6PrefixLen: Long,
        @SerialName("IPAddress") val ipAddress: String,
        @SerialName("IPPrefixLen") val ipPrefixLen: Long,
        @SerialName("IPv6Gateway") val ipv6Gateway: String,
        @SerialName("MacAddress") val macAddress: String,
        @SerialName("Networks") val networks: Map<String, ContainerConfig.Networking.EndpointSettings>
    ) {
        @Serializable(with = PortMapSerializer::class)
        @JvmInline
        value class PortMap(
            val ports: Map<String, List<PortBinding>> = emptyMap()
        )

        @Serializable
        data class PortBinding(
            @SerialName("HostIp") val hostIp: String,
            @SerialName("HostPort") val hostPort: String
        )

        @Serializable
        data class Address(
            @SerialName("Addr") val addr: String,
            @SerialName("PrefixLen") val prefixLen: Int
        )
    }
}
