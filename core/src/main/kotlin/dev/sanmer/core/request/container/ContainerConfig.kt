package dev.sanmer.core.request.container

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContainerConfig(
    @SerialName("Hostname")
    val hostname: String,
    @SerialName("Domainname")
    val domainName: String,
    @SerialName("User")
    val user: String,
    @SerialName("AttachStdin")
    val attachStdin: Boolean = false,
    @SerialName("AttachStdout")
    val attachStdout: Boolean = true,
    @SerialName("AttachStderr")
    val attachStderr: Boolean = true,
    @SerialName("ExposedPorts")
    val exposedPorts: Map<String, ExposedPortValue> = emptyMap(),
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
    val healthCheck: Health = Health(),
    @SerialName("ArgsEscaped")
    val argsEscaped: Boolean = false,
    @SerialName("Image")
    val image: String,
    @SerialName("Volumes")
    val volumes: Map<String, VolumeValue> = emptyMap(),
    @SerialName("WorkingDir")
    val workingDir: String,
    @SerialName("Entrypoint")
    val entryPoint: List<String>,
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
    val shell: List<String> = emptyList(),
    @SerialName("HostConfig")
    val hostConfig: HostConfig,
    @SerialName("NetworkingConfig")
    val networkingConfig: Networking
) {
    @Serializable
    @JvmInline
    value class ExposedPortValue(
        val value: Map<String, String> = emptyMap()
    )

    @Serializable
    data class Health(
        @SerialName("Test")
        val test: List<String> = emptyList(),
        @SerialName("Interval")
        val interval: Long = 0,
        @SerialName("Timeout")
        val timeout: Long = 0,
        @SerialName("Retries")
        val retries: Int = 0,
        @SerialName("StartPeriod")
        val startPeriod: Long = 0,
        @SerialName("StartInterval")
        val startInterval: Long = 0
    )

    @Serializable
    @JvmInline
    value class VolumeValue(
        val value: Map<String, String> = emptyMap()
    )

    @Serializable
    data class Networking(
        @SerialName("EndpointsConfig")
        val endpointsConfig: Map<String, EndpointSettings>
    ) {
        @Serializable
        data class EndpointSettings(
            @SerialName("IPAMConfig")
            val ipAMConfig: IPAMConfig = IPAMConfig(),
            @SerialName("Links")
            val links: List<String> = emptyList(),
            @SerialName("MacAddress")
            val macAddress: String,
            @SerialName("Aliases")
            val aliases: List<String> = emptyList(),
            @SerialName("DriverOpts")
            val driverOpts: Map<String, String> = emptyMap(),
            @SerialName("GwPriority")
            val gwPriority: Int,
            @SerialName("NetworkID")
            val networkId: String,
            @SerialName("EndpointID")
            val endpointId: String,
            @SerialName("Gateway")
            val gateway: String,
            @SerialName("IPAddress")
            val ipAddress: String,
            @SerialName("IPPrefixLen")
            val ipPrefixLen: Long,
            @SerialName("IPv6Gateway")
            val ipv6Gateway: String,
            @SerialName("GlobalIPv6Address")
            val globalIPv6Address: String,
            @SerialName("GlobalIPv6PrefixLen")
            val globalIPv6PrefixLen: Long,
            @SerialName("DNSNames")
            val dnsNames: List<String> = emptyList()
        ) {
            @Serializable
            data class IPAMConfig(
                @SerialName("IPv4Address")
                val ipv4Address: String = "",
                @SerialName("IPv6Address")
                val ipv6Address: String = "",
                @SerialName("LinkLocalIPs")
                val linkLocalIPs: List<String> = emptyList()
            )
        }
    }
}