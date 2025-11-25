package dev.sanmer.core.response.container

import dev.sanmer.core.request.container.ContainerConfig
import dev.sanmer.core.request.container.HostConfig
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ContainerLowLevel(
    @SerialName("Id")
    val id: String,
    @SerialName("Created")
    @Contextual
    val created: Instant = Instant.fromEpochMilliseconds(0),
    @SerialName("Path")
    val path: String,
    @SerialName("Args")
    val args: List<String>,
    @SerialName("State")
    val state: State = State(),
    @SerialName("Image")
    val image: String,
    @SerialName("ResolvConfPath")
    val resolvConfPath: String,
    @SerialName("HostnamePath")
    val hostnamePath: String,
    @SerialName("HostsPath")
    val hostsPath: String,
    @SerialName("LogPath")
    val logPath: String = "",
    @SerialName("Name")
    val name: String,
    @SerialName("RestartCount")
    val restartCount: Int,
    @SerialName("Driver")
    val driver: String,
    @SerialName("Platform")
    val platform: String,
    @SerialName("MountLabel")
    val mountLabel: String,
    @SerialName("ProcessLabel")
    val processLabel: String,
    @SerialName("AppArmorProfile")
    val appArmorProfile: String,
    @SerialName("ExecIDs")
    val execIDs: List<String> = emptyList(),
    @SerialName("HostConfig")
    val hostConfig: HostConfig,
    @SerialName("GraphDriver")
    val graphDriver: DriverData,
    @SerialName("SizeRw")
    val sizeRw: Long = 0,
    @SerialName("SizeRootFs")
    val sizeRootFs: Long = 0,
    @SerialName("Mounts")
    val mounts: List<MountPoint>,
    @SerialName("Config")
    val config: Config,
    @SerialName("NetworkSettings")
    val networkSettings: NetworkSettings
) {
    @Serializable
    data class State(
        @SerialName("Status")
        val status: Container.State = Container.State.Dead,
        @SerialName("Running")
        val running: Boolean = false,
        @SerialName("Paused")
        val paused: Boolean = false,
        @SerialName("Restarting")
        val restarting: Boolean = false,
        @SerialName("OOMKilled")
        val ooMKilled: Boolean = true,
        @SerialName("Dead")
        val dead: Boolean = true,
        @SerialName("Pid")
        val pid: Int = 0,
        @SerialName("ExitCode")
        val exitCode: Int = 0,
        @SerialName("Error")
        val error: String = "",
        @SerialName("StartedAt")
        @Contextual
        val startedAt: Instant = Instant.fromEpochMilliseconds(0),
        @SerialName("FinishedAt")
        @Contextual
        val finishedAt: Instant = Instant.fromEpochMilliseconds(0),
        @SerialName("Health")
        val health: Health = Health()
    ) {
        @Serializable
        data class Health(
            @SerialName("Status")
            val status: String = "",
            @SerialName("FailingStreak")
            val failingStreak: Int = 0,
            @SerialName("Log")
            val log: List<CheckResult> = emptyList()
        ) {
            @Serializable
            data class CheckResult(
                @SerialName("Start")
                @Contextual
                val start: Instant,
                @SerialName("End")
                @Contextual
                val end: Instant,
                @SerialName("ExitCode")
                val exitCode: Int,
                @SerialName("Output")
                val output: String
            )
        }
    }

    @Serializable
    data class DriverData(
        @SerialName("Name")
        val name: String,
        @SerialName("Data")
        val data: Map<String, String>
    )

    @Serializable
    data class Config(
        @SerialName("Hostname")
        val hostname: String = "",
        @SerialName("Domainname")
        val domainName: String = "",
        @SerialName("User")
        val user: String = "",
        @SerialName("AttachStdin")
        val attachStdin: Boolean = false,
        @SerialName("AttachStdout")
        val attachStdout: Boolean = true,
        @SerialName("AttachStderr")
        val attachStderr: Boolean = true,
        @SerialName("ExposedPorts")
        val exposedPorts: Map<String, ContainerConfig.ExposedPortValue> = emptyMap(),
        @SerialName("Tty")
        val tty: Boolean = false,
        @SerialName("OpenStdin")
        val openStdin: Boolean = false,
        @SerialName("StdinOnce")
        val stdinOnce: Boolean = false,
        @SerialName("Env")
        val env: List<String> = emptyList(),
        @SerialName("Cmd")
        val cmd: List<String> = emptyList(),
        @SerialName("Healthcheck")
        val healthCheck: ContainerConfig.Health = ContainerConfig.Health(),
        @SerialName("ArgsEscaped")
        val argsEscaped: Boolean = false,
        @SerialName("Image")
        val image: String = "",
        @SerialName("Volumes")
        val volumes: Map<String, ContainerConfig.VolumeValue> = emptyMap(),
        @SerialName("WorkingDir")
        val workingDir: String = "",
        @SerialName("Entrypoint")
        val entryPoint: List<String> = emptyList(),
        @SerialName("NetworkDisabled")
        val networkDisabled: Boolean = false,
        @SerialName("OnBuild")
        val onBuild: List<String> = emptyList(),
        @SerialName("Labels")
        val labels: Map<String, String> = emptyMap(),
        @SerialName("StopSignal")
        val stopSignal: String = "",
        @SerialName("StopTimeout")
        val stopTimeout: Int = 10,
        @SerialName("Shell")
        val shell: List<String> = emptyList()
    )

    @Serializable
    data class MountPoint(
        @SerialName("Type")
        val type: HostConfig.Mount.Type,
        @SerialName("Name")
        val name: String = "",
        @SerialName("Source")
        val source: String,
        @SerialName("Destination")
        val destination: String,
        @SerialName("Driver")
        val driver: String = "",
        @SerialName("Mode")
        val mode: String,
        @SerialName("RW")
        val rw: Boolean,
        @SerialName("Propagation")
        val propagation: String
    )

    @Serializable
    data class NetworkSettings(
        @SerialName("SandboxID")
        val sandboxId: String,
        @SerialName("Ports")
        val ports: Map<String, List<HostConfig.PortBinding>?> = emptyMap(),
        @SerialName("SandboxKey")
        val sandboxKey: String,
        @SerialName("Networks")
        val networks: Map<String, ContainerConfig.Networking.EndpointSettings>
    )
}
