package dev.sanmer.whalya.repository

import dev.sanmer.core.response.container.Container
import dev.sanmer.core.response.container.ContainerLog
import dev.sanmer.core.response.container.ContainerLowLevel
import dev.sanmer.core.response.container.ContainerPruned
import dev.sanmer.core.response.container.ContainerStats
import dev.sanmer.core.response.image.Image
import dev.sanmer.core.response.image.ImageHistory
import dev.sanmer.core.response.image.ImageLowLevel
import dev.sanmer.core.response.image.ImagePruned
import dev.sanmer.core.response.image.ImageRemoved
import dev.sanmer.core.response.network.Network
import dev.sanmer.core.response.network.NetworkPruned
import dev.sanmer.core.response.system.SystemInfo
import dev.sanmer.core.response.system.SystemVersion
import dev.sanmer.core.response.volume.Volume
import dev.sanmer.core.response.volume.VolumePruned
import kotlinx.coroutines.flow.StateFlow

interface RemoteRepository {
    val containersFlow: StateFlow<List<Container>>
    val imagesFlow: StateFlow<List<Image>>
    val networksFlow: StateFlow<List<Network>>
    val volumesFlow: StateFlow<List<Volume>>
    suspend fun fetchContainers()
    suspend fun fetchImages()
    suspend fun fetchNetworks()
    suspend fun fetchVolumes()
    suspend fun info(): SystemInfo
    suspend fun version(): SystemVersion

    suspend fun pruneContainers(): ContainerPruned
    suspend fun pruneImages(): ImagePruned
    suspend fun pruneNetworks(): NetworkPruned
    suspend fun pruneVolumes(): VolumePruned

    suspend fun inspectContainer(id: String): ContainerLowLevel
    suspend fun startContainer(id: String)
    suspend fun stopContainer(id: String)
    suspend fun pauseContainer(id: String)
    suspend fun unpauseContainer(id: String)
    suspend fun restartContainer(id: String)
    suspend fun removeContainer(id: String)
    suspend fun upContainer(container: ContainerLowLevel)
    suspend fun containerLogs(id: String): List<ContainerLog>
    suspend fun containersStats(id: String): ContainerStats

    suspend fun inspectImage(id: String): ImageLowLevel
    suspend fun fetchHistories(image: ImageLowLevel): List<ImageHistory>
    suspend fun pullImage(image: ImageLowLevel)
    suspend fun removeImage(id: String): List<ImageRemoved>

    suspend fun inspectNetwork(id: String): Network
    suspend fun removeNetwork(id: String)

    suspend fun inspectVolume(name: String): Volume
    suspend fun removeVolume(name: String)
}