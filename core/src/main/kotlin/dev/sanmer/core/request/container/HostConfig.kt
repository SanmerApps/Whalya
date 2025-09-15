package dev.sanmer.core.request.container

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HostConfig(
    @SerialName("CpuShares")
    val cpuShares: Int,
    @SerialName("Memory")
    val memory: Long = 0,
    @SerialName("CgroupParent")
    val cgroupParent: String,
    @SerialName("BlkioWeight")
    val blkioWeight: Int,
    @SerialName("BlkioWeightDevice")
    val blkioWeightDevice: List<WeightDevice> = emptyList(),
    @SerialName("BlkioDeviceReadBps")
    val blkioDeviceReadBps: List<ThrottleDevice> = emptyList(),
    @SerialName("BlkioDeviceWriteBps")
    val blkioDeviceWriteBps: List<ThrottleDevice> = emptyList(),
    @SerialName("BlkioDeviceReadIOps")
    val blkioDeviceReadIOps: List<ThrottleDevice> = emptyList(),
    @SerialName("BlkioDeviceWriteIOps")
    val blkioDeviceWriteIOps: List<ThrottleDevice> = emptyList(),
    @SerialName("CpuPeriod")
    val cpuPeriod: Long,
    @SerialName("CpuQuota")
    val cpuQuota: Long,
    @SerialName("CpuRealtimePeriod")
    val cpuRealtimePeriod: Long,
    @SerialName("CpuRealtimeRuntime")
    val cpuRealtimeRuntime: Long,
    @SerialName("CpusetCpus")
    val cpusetCpus: String,
    @SerialName("CpusetMems")
    val cpusetMems: String,
    @SerialName("Devices")
    val devices: List<DeviceMapping> = emptyList(),
    @SerialName("DeviceCgroupRules")
    val deviceCgroupRules: List<String> = emptyList(),
    @SerialName("DeviceRequests")
    val deviceRequests: List<DeviceRequest> = emptyList(),
    @SerialName("KernelMemoryTCP")
    val kernelMemoryTCP: Long? = null,
    @SerialName("MemoryReservation")
    val memoryReservation: Long,
    @SerialName("MemorySwap")
    val memorySwap: Long,
    @SerialName("MemorySwappiness")
    val memorySwappiness: Long? = null,
    @SerialName("NanoCpus")
    val nanoCpus: Long,
    @SerialName("OomKillDisable")
    val oomKillDisable: Boolean = false,
    @SerialName("Init")
    val init: Boolean? = null,
    @SerialName("PidsLimit")
    val pidsLimit: Int? = null,
    @SerialName("Ulimits")
    val ulimits: List<Ulimit> = emptyList(),
    @SerialName("Binds")
    val binds: List<String> = emptyList(),
    @SerialName("ContainerIDFile")
    val containerIDFile: String,
    @SerialName("LogConfig")
    val logConfig: LogConfig,
    @SerialName("NetworkMode")
    val networkMode: String,
    @SerialName("PortBindings")
    val ports: Map<String, List<PortBinding>?> = emptyMap(),
    @SerialName("RestartPolicy")
    val restartPolicy: RestartPolicy,
    @SerialName("AutoRemove")
    val autoRemove: Boolean,
    @SerialName("VolumeDriver")
    val volumeDriver: String,
    @SerialName("VolumesFrom")
    val volumesFrom: List<String> = emptyList(),
    @SerialName("Mounts")
    val mounts: List<Mount> = emptyList(),
    @SerialName("ConsoleSize")
    val consoleSize: List<Int> = emptyList(),
    @SerialName("Annotations")
    val annotations: Map<String, String> = emptyMap(),
    @SerialName("CapAdd")
    val capAdd: List<String> = emptyList(),
    @SerialName("CapDrop")
    val capDrop: List<String> = emptyList(),
    @SerialName("CgroupnsMode")
    val cgroupnsMode: CgroupnsMode,
    @SerialName("Dns")
    val dns: List<String> = emptyList(),
    @SerialName("DnsOptions")
    val dnsOptions: List<String> = emptyList(),
    @SerialName("DnsSearch")
    val dnsSearch: List<String> = emptyList(),
    @SerialName("ExtraHosts")
    val extraHosts: List<String> = emptyList(),
    @SerialName("GroupAdd")
    val groupAdd: List<String> = emptyList(),
    @SerialName("IpcMode")
    val ipcMode: String,
    @SerialName("Cgroup")
    val cgroup: String,
    @SerialName("Links")
    val links: List<String> = emptyList(),
    @SerialName("OomScoreAdj")
    val oomScoreAdj: Int,
    @SerialName("PidMode")
    val pidMode: String,
    @SerialName("Privileged")
    val privileged: Boolean,
    @SerialName("PublishAllPorts")
    val publishAllPorts: Boolean,
    @SerialName("ReadonlyRootfs")
    val readonlyRootfs: Boolean,
    @SerialName("SecurityOpt")
    val securityOpt: List<String> = emptyList(),
    @SerialName("StorageOpt")
    val storageOpt: Map<String, String> = emptyMap(),
    @SerialName("Tmpfs")
    val tmpfs: Map<String, String> = emptyMap(),
    @SerialName("UTSMode")
    val utsMode: String,
    @SerialName("UsernsMode")
    val usernsMode: String,
    @SerialName("ShmSize")
    val shmSize: Long,
    @SerialName("Sysctls")
    val sysctls: Map<String, String> = emptyMap(),
    @SerialName("Runtime")
    val runtime: String? = null,
    @SerialName("MaskedPaths")
    val maskedPaths: List<String> = emptyList(),
    @SerialName("ReadonlyPaths")
    val readonlyPaths: List<String> = emptyList()
) {
    @Serializable
    data class WeightDevice(
        @SerialName("Path")
        val path: String,
        @SerialName("Weight")
        val weight: Int
    )

    @Serializable
    data class ThrottleDevice(
        @SerialName("Path")
        val path: String,
        @SerialName("Rate")
        val rate: Long
    )

    @Serializable
    data class DeviceMapping(
        @SerialName("PathOnHost")
        val pathOnHost: String,
        @SerialName("PathInContainer")
        val pathInContainer: String,
        @SerialName("CgroupPermissions")
        val cgroupPermissions: String
    )

    @Serializable
    data class DeviceRequest(
        @SerialName("Driver")
        val driver: String,
        @SerialName("Count")
        val count: Int,
        @SerialName("DeviceIDs")
        val deviceIDs: List<String>,
        @SerialName("Capabilities")
        val capabilities: List<String>,
        @SerialName("Options")
        val options: Map<String, String>
    )

    @Serializable
    data class Ulimit(
        @SerialName("Name")
        val name: String,
        @SerialName("Soft")
        val soft: Int,
        @SerialName("Hard")
        val hard: Int
    )

    @Serializable
    data class LogConfig(
        @SerialName("Type")
        val type: Type,
        @SerialName("Config")
        val config: Map<String, String>
    ) {
        @Serializable
        enum class Type {
            @SerialName("local")
            Local,

            @SerialName("json-file")
            JsonFile,

            @SerialName("syslog")
            SysLog,

            @SerialName("journald")
            Journald,

            @SerialName("gelf")
            Gelf,

            @SerialName("fluentd")
            Fluentd,

            @SerialName("awslogs")
            AwsLogs,

            @SerialName("splunk")
            Splunk,

            @SerialName("etwlogs")
            EtwLogs,

            @SerialName("none")
            None
        }
    }

    @Serializable
    data class PortBinding(
        @SerialName("HostIp")
        val hostIp: String,
        @SerialName("HostPort")
        val hostPort: String
    )

    @Serializable
    data class RestartPolicy(
        @SerialName("Name")
        val name: Name,
        @SerialName("MaximumRetryCount")
        val maximumRetryCount: Int
    ) {
        @Serializable
        enum class Name {
            @SerialName("")
            None,

            @SerialName("no")
            No,

            @SerialName("always")
            Always,

            @SerialName("unless-stopped")
            UnlessStopped,

            @SerialName("on-failure")
            OnFailure;

            override fun toString(): String {
                return when (this) {
                    None -> ""
                    No -> "no"
                    Always -> "always"
                    UnlessStopped -> "unless-stopped"
                    OnFailure -> "on-failure"
                }
            }
        }
    }

    @Serializable
    data class Mount(
        @SerialName("Target")
        val target: String,
        @SerialName("Source")
        val source: String,
        @SerialName("Type")
        val type: Type,
        @SerialName("ReadOnly")
        val readOnly: Boolean? = null,
        @SerialName("Consistency")
        val consistency: String? = null,
        @SerialName("BindOptions")
        val bind: Bind? = null,
        @SerialName("VolumeOptions")
        val volume: Volume? = null,
        @SerialName("ImageOptions")
        val image: Image? = null,
        @SerialName("TmpfsOptions")
        val tmpfs: Tmpfs? = null
    ) {
        @Serializable
        enum class Type {
            @SerialName("bind")
            Bind,

            @SerialName("volume")
            Volume,

            @SerialName("image")
            Image,

            @SerialName("tmpfs")
            Tmpfs,

            @SerialName("npipe")
            Npipe,

            @SerialName("cluster")
            Cluster
        }

        @Serializable
        data class Bind(
            @SerialName("Propagation")
            val propagation: Propagation,
            @SerialName("NonRecursive")
            val nonRecursive: Boolean = false,
            @SerialName("CreateMountpoint")
            val createMount: Boolean = false,
            @SerialName("ReadOnlyNonRecursive")
            val readOnlyNonRecursive: Boolean = false,
            @SerialName("ReadOnlyForceRecursive")
            val readOnlyForceRecursive: Boolean = false
        ) {
            @Serializable
            enum class Propagation {
                @SerialName("private")
                Private,

                @SerialName("rprivate")
                RPrivate,

                @SerialName("shared")
                Shared,

                @SerialName("rshared")
                RShared,

                @SerialName("slave")
                Slave,

                @SerialName("rslave")
                RSlave
            }
        }

        @Serializable
        data class Volume(
            @SerialName("NoCopy")
            val noCopy: Boolean = false,
            @SerialName("Labels")
            val labels: Map<String, String> = emptyMap(),
            @SerialName("DriverConfig")
            val driverConfig: DriverConfig,
            @SerialName("Subpath")
            val subpath: String
        ) {
            @Serializable
            data class DriverConfig(
                @SerialName("Name")
                val name: String,
                @SerialName("Options")
                val options: Map<String, String> = emptyMap(),
            )
        }

        @Serializable
        data class Image(
            @SerialName("Subpath")
            val subpath: String
        )

        @Serializable
        data class Tmpfs(
            @SerialName("sizeBytes")
            val sizeBytes: Long,
            @SerialName("Mode")
            val mode: Int,
            @SerialName("Options")
            val options: List<List<String>>
        )
    }

    @Serializable
    enum class CgroupnsMode {
        @SerialName("private")
        Private,

        @SerialName("host")
        Host
    }
}
