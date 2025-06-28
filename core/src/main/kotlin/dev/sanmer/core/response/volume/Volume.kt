package dev.sanmer.core.response.volume

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Volume(
    @SerialName("Name")
    val name: String,
    @SerialName("Driver")
    val driver: String,
    @SerialName("Mountpoint")
    val mountPoint: String,
    @SerialName("CreatedAt")
    @Contextual
    val createdAt: Instant,
    @SerialName("Status")
    val status: Map<String, String> = emptyMap(),
    @SerialName("Labels")
    val labels: Map<String, String> = emptyMap(),
    @SerialName("Scope")
    val scope: Scope = Scope.Local,
    @SerialName("Options")
    val options: Map<String, String> = emptyMap(),
    @SerialName("UsageData")
    val usageData: UsageData = UsageData()
) {
    @Serializable
    enum class Scope {
        @SerialName("local")
        Local,

        @SerialName("global")
        Global
    }

    @Serializable
    data class UsageData(
        @SerialName("Size")
        val size: Long = 0,
        @SerialName("RefCount")
        val refCount: Long = 0
    )
}