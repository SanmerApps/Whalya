package dev.sanmer.core.response.container

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContainerStats(
    @SerialName("name")
    val name: String,
    @SerialName("id")
    val id: String,
    @SerialName("read")
    val read: Instant,
    @SerialName("preread")
    val preRead: Instant,
    @SerialName("pids_stats")
    val pidsStats: PidsStats,
    @SerialName("blkio_stats")
    val blkioStats: BlkioStats,
    @SerialName("cpu_stats")
    val cpuStats: CPUStats,
    @SerialName("precpu_stats")
    val preCpuStats: CPUStats,
    @SerialName("memory_stats")
    val memoryStats: MemoryStats,
    @SerialName("networks")
    val networks: Map<String, NetworkStats> = emptyMap()
) {
    @Serializable
    data class PidsStats(
        @SerialName("current")
        val current: Long,
        @SerialName("limit")
        val limit: Long
    )

    @Serializable
    data class BlkioStats(
        @SerialName("io_service_bytes_recursive")
        val ioServiceBytesRecursive: List<BlkioStatEntry> = emptyList(),
        @SerialName("io_serviced_recursive")
        val ioServicedRecursive: List<BlkioStatEntry> = emptyList(),
        @SerialName("io_queue_recursive")
        val ioQueueRecursive: List<BlkioStatEntry> = emptyList(),
        @SerialName("io_service_time_recursive")
        val ioServiceTimeRecursive: List<BlkioStatEntry> = emptyList(),
        @SerialName("io_wait_time_recursive")
        val ioWaitTimeRecursive: List<BlkioStatEntry> = emptyList(),
        @SerialName("io_merged_recursive")
        val ioMergedRecursive: List<BlkioStatEntry> = emptyList(),
        @SerialName("io_time_recursive")
        val ioTimeRecursive: List<BlkioStatEntry> = emptyList(),
        @SerialName("sectors_recursive")
        val sectorsRecursive: List<BlkioStatEntry> = emptyList()
    ) {
        @Serializable
        data class BlkioStatEntry(
            @SerialName("major")
            val major: Long,
            @SerialName("minor")
            val minor: Long,
            @SerialName("op")
            val op: String,
            @SerialName("value")
            val value: Long
        )
    }

    @Serializable
    data class CPUStats(
        @SerialName("cpu_usage")
        val cpuUsage: Usage,
        @SerialName("system_cpu_usage")
        val systemCpuUsage: Long,
        @SerialName("online_cpus")
        val onlineCpus: Long
    ) {
        @Serializable
        data class Usage(
            @SerialName("total_usage")
            val totalUsage: Long,
            @SerialName("usage_in_kernelmode")
            val usageInKernelMode: Long,
            @SerialName("usage_in_usermode")
            val usageInUserMode: Long
        )
    }

    @Serializable
    data class MemoryStats(
        @SerialName("usage")
        val usage: Long,
        @SerialName("limit")
        val limit: Long
    )

    @Serializable
    data class NetworkStats(
        @SerialName("rx_bytes")
        val rxBytes: Long,
        @SerialName("rx_packets")
        val rxPackets: Long,
        @SerialName("rx_errors")
        val rxErrors: Long,
        @SerialName("rx_dropped")
        val rxDropped: Long,
        @SerialName("tx_bytes")
        val txBytes: Long,
        @SerialName("tx_packets")
        val txPackets: Long,
        @SerialName("tx_errors")
        val txErrors: Long,
        @SerialName("tx_dropped")
        val txDropped: Long
    )
}