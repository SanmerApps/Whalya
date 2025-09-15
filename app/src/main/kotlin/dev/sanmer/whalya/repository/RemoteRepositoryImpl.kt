package dev.sanmer.whalya.repository

import dev.sanmer.core.Docker.infiniteTimeout
import dev.sanmer.core.JsonCompat.encodeJson
import dev.sanmer.core.converter.toConfig
import dev.sanmer.core.request.image.OCIPlatform
import dev.sanmer.core.resource.Containers
import dev.sanmer.core.resource.Images
import dev.sanmer.core.resource.Networks
import dev.sanmer.core.resource.System
import dev.sanmer.core.resource.Volumes
import dev.sanmer.core.response.container.Container
import dev.sanmer.core.response.container.ContainerCreated
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
import dev.sanmer.core.response.volume.VolumeList
import dev.sanmer.core.response.volume.VolumePruned
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RemoteRepositoryImpl(
    private val client: () -> HttpClient
) : RemoteRepository {
    private val _containersFlow = MutableStateFlow<List<Container>>(emptyList())
    override val containersFlow = _containersFlow.asStateFlow()

    private val _imagesFlow = MutableStateFlow<List<Image>>(emptyList())
    override val imagesFlow = _imagesFlow.asStateFlow()

    private val _networksFlow = MutableStateFlow<List<Network>>(emptyList())
    override val networksFlow = _networksFlow.asStateFlow()

    private val _volumesFlow = MutableStateFlow<List<Volume>>(emptyList())
    override val volumesFlow = _volumesFlow.asStateFlow()

    override suspend fun fetchContainers() {
        _containersFlow.update {
            client().get(Containers.All(all = true))
                .body<List<Container>>()
        }
    }

    override suspend fun fetchImages() {
        _imagesFlow.update {
            client().get(Images.All(all = true))
                .body<List<Image>>()
        }
    }

    override suspend fun fetchNetworks() {
        _networksFlow.update {
            client().get(Networks())
                .body<List<Network>>()
        }
    }

    override suspend fun fetchVolumes() {
        _volumesFlow.update {
            client().get(Volumes())
                .body<VolumeList>()
                .volumes
        }
    }

    override suspend fun info(): SystemInfo {
        return client().get(System.Info()).body()
    }

    override suspend fun version(): SystemVersion {
        return client().get(System.Version()).body()
    }

    override suspend fun pruneContainers(): ContainerPruned {
        return client().post(Containers.Prune()) {
            infiniteTimeout()
        }.body()
    }

    override suspend fun pruneImages(): ImagePruned {
        return client().post(Images.Prune()) {
            infiniteTimeout()
        }.body()
    }

    override suspend fun pruneNetworks(): NetworkPruned {
        return client().post(Networks.Prune()) {
            infiniteTimeout()
        }.body()
    }

    override suspend fun pruneVolumes(): VolumePruned {
        return client().post(Volumes.Prune()) {
            infiniteTimeout()
        }.body()
    }

    override suspend fun inspectContainer(id: String): ContainerLowLevel {
        return client().get(Containers.Inspect(id = id, size = true)).body()
    }

    override suspend fun startContainer(id: String) {
        client().post(Containers.Start(id = id))
    }

    override suspend fun stopContainer(id: String) {
        client().post(Containers.Stop(id = id))
    }

    override suspend fun pauseContainer(id: String) {
        client().post(Containers.Pause(id = id))
    }

    override suspend fun unpauseContainer(id: String) {
        client().post(Containers.Unpause(id = id))
    }

    override suspend fun restartContainer(id: String) {
        client().post(Containers.Restart(id = id))
    }

    override suspend fun removeContainer(id: String) {
        client().delete(Containers.Remove(id = id))
    }

    override suspend fun upContainer(container: ContainerLowLevel) {
        val config = container.toConfig()
        client().post(Containers.Stop(id = container.id))
        client().delete(Containers.Remove(id = container.id))
        val created = client().post(Containers.Create(name = container.name)) {
            contentType(ContentType.Application.Json)
            setBody(config)
        }.body<ContainerCreated>()
        client().post(Containers.Start(id = created.id))
    }

    override suspend fun containerLogs(id: String): List<ContainerLog> {
        return client().get(
            Containers.Logs(
                id = id,
                follow = false,
                stdout = true,
                stderr = true,
                tail = "1000"
            )
        ).body()
    }

    override suspend fun containersStats(id: String): ContainerStats {
        return client().get(
            Containers.Stats(
                id = id,
                stream = false,
                oneShot = false
            )
        ).body()
    }

    override suspend fun inspectImage(id: String): ImageLowLevel {
        return client().get(Images.Inspect(id = id)).body()
    }

    override suspend fun fetchHistories(image: ImageLowLevel): List<ImageHistory> {
        return client().get(
            Images.History(
                id = image.id,
                platform = OCIPlatform(
                    architecture = image.architecture,
                    os = image.os,
                    variant = image.variant
                ).encodeJson()
            )
        ).body()
    }

    override suspend fun pullImage(image: ImageLowLevel) {
        client().post(Images.Create(fromImage = image.repoTags[0])) {
            infiniteTimeout()
        }
    }

    override suspend fun removeImage(id: String): List<ImageRemoved> {
        return client().delete(Images.Remove(id = id)).body()
    }

    override suspend fun inspectNetwork(id: String): Network {
        return client().get(Networks.Inspect(id = id)).body()
    }

    override suspend fun removeNetwork(id: String) {
        client().delete(Networks.Remove(id = id))
    }

    override suspend fun inspectVolume(name: String): Volume {
        return client().get(Volumes.Inspect(name = name)).body()
    }

    override suspend fun removeVolume(name: String) {
        client().delete(Volumes.Remove(name = name))
    }
}