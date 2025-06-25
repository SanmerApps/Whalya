package dev.sanmer.core.response.system

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class SystemVersion(
    @SerialName("Platform")
    val platform: Platform,
    @SerialName("Components")
    val components: List<Component>,
    @SerialName("Version")
    val version: String,
    @SerialName("ApiVersion")
    val apiVersion: String,
    @SerialName("MinAPIVersion")
    val minApiVersion: String,
    @SerialName("GitCommit")
    val gitCommit: String,
    @SerialName("GoVersion")
    val goVersion: String,
    @SerialName("Os")
    val os: String,
    @SerialName("Arch")
    val arch: String,
    @SerialName("KernelVersion")
    val kernelVersion: String = "",
    @SerialName("Experimental")
    val experimental: Boolean = false,
    @SerialName("BuildTime")
    @Contextual
    val buildTime: Instant
) {
    @Serializable
    data class Platform(
        @SerialName("Name")
        val name: String
    )

    @Serializable
    data class Component(
        @SerialName("Name")
        val name: String,
        @SerialName("Version")
        val version: String
    )
}