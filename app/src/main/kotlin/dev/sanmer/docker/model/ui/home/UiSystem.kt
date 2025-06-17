package dev.sanmer.docker.model.ui.home

import dev.sanmer.core.docker.response.system.SystemInfo
import dev.sanmer.core.docker.response.system.SystemVersion
import dev.sanmer.docker.ktx.copy
import dev.sanmer.docker.ktx.sizeByIEC
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class UiSystem(
    val original: SystemInfo,
    val version: SystemVersion
) {
    val driver: String
        inline get() = original.driver

    val dockerRootDir: String
        inline get() = original.dockerRootDir

    val operatingSystem: String
        inline get() = original.operatingSystem

    val kernelVersion: String
        inline get() = original.kernelVersion

    val apiVersion: String
        inline get() = "${version.minApiVersion} - ${version.apiVersion}"

    val platform by lazy {
        val nCpu = "${original.nCpu}c"
        val memTotal = original.memTotal.sizeByIEC()
        listOf(nCpu, memTotal, original.osType, original.architecture)
    }

    val goVersion by lazy {
        version.goVersion.removePrefix("go")
    }

    val buildTime by lazy {
        version.buildTime.toLocalDateTime(TimeZone.currentSystemDefault())
            .copy(nanosecond = 0)
            .toString()
    }

    val components by lazy {
        version.components.map { "${it.name.lowercase()} - ${it.version}" }
    }
}
