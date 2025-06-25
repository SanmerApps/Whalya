package dev.sanmer.core.response.system

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class SystemInfo(
    @SerialName("Containers")
    val containers: Int,
    @SerialName("ContainersRunning")
    val containersRunning: Int,
    @SerialName("ContainersPaused")
    val containersPaused: Int,
    @SerialName("ContainersStopped")
    val containersStopped: Int,
    @SerialName("Images")
    val images: Int,
    @SerialName("Driver")
    val driver: String,
    @SerialName("DockerRootDir")
    val dockerRootDir: String,
    @SerialName("Plugins")
    val plugins: Plugins,
    @SerialName("Debug")
    val debug: Boolean,
    @SerialName("SystemTime")
    @Contextual
    val systemTime: Instant,
    @SerialName("LoggingDriver")
    val loggingDriver: String,
    @SerialName("KernelVersion")
    val kernelVersion: String,
    @SerialName("OperatingSystem")
    val operatingSystem: String,
    @SerialName("OSVersion")
    val osVersion: String,
    @SerialName("OSType")
    val osType: String,
    @SerialName("Architecture")
    val architecture: String,
    @SerialName("NCPU")
    val nCpu: Int,
    @SerialName("MemTotal")
    val memTotal: Long,
    @SerialName("Name")
    val name: String
) {
    @Serializable
    data class Plugins(
        @SerialName("Volume")
        val volume: List<String>,
        @SerialName("Network")
        val network: List<String>,
        @SerialName("Authorization")
        val authorization: List<String> = emptyList(),
        @SerialName("Log")
        val log: List<String>
    )
}
