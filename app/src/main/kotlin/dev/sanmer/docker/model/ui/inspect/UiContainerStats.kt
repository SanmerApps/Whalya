package dev.sanmer.docker.model.ui.inspect

import dev.sanmer.core.response.container.ContainerStats
import dev.sanmer.docker.ktx.sizeByIEC
import dev.sanmer.docker.ktx.sizeBySI
import dev.sanmer.docker.model.ui.home.UiContainer.Default.name
import dev.sanmer.docker.model.ui.home.UiContainer.Default.shortId
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class UiContainerStats(
    val original: ContainerStats
) {
    val pids: Long
        inline get() = original.pidsStats.current

    val name by lazy {
        original.name.name()
    }

    val readAt by lazy {
        original.read.toLocalDateTime(TimeZone.currentSystemDefault()).time
    }

    val id by lazy {
        original.id.shortId()
    }

    val cpuPercent by lazy {
        val cpuDelta = original.cpuStats.cpuUsage.totalUsage -
                original.preCpuStats.cpuUsage.totalUsage
        val systemDelta = original.cpuStats.systemCpuUsage -
                original.preCpuStats.systemCpuUsage
        (cpuDelta / systemDelta.toFloat()) * original.cpuStats.onlineCpus
    }

    val memoryUsage by lazy {
        original.memoryStats.usage.sizeByIEC()
    }

    val memoryLimit by lazy {
        original.memoryStats.limit.sizeByIEC()
    }

    val memoryPercent by lazy {
        original.memoryStats.usage / original.memoryStats.limit.toFloat()
    }

    val networkReceived by lazy {
        original.networks.values.sumOf { it.rxBytes }.sizeBySI()

    }

    val networkSent by lazy {
        original.networks.values.sumOf { it.txBytes }.sizeBySI()
    }

    val blockRead by lazy {
        original.blkioStats.ioServiceBytesRecursive.filter { it.op == "read" }
            .sumOf { it.value }
            .sizeBySI()
    }

    val blockWrite by lazy {
        original.blkioStats.ioServiceBytesRecursive.filter { it.op == "write" }
            .sumOf { it.value }
            .sizeBySI()
    }
}