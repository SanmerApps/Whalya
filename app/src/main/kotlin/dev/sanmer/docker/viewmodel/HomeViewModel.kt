package dev.sanmer.docker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.core.Docker.get
import dev.sanmer.core.Docker.post
import dev.sanmer.core.resource.Containers
import dev.sanmer.core.resource.Images
import dev.sanmer.core.resource.Networks
import dev.sanmer.core.resource.System
import dev.sanmer.core.resource.Volumes
import dev.sanmer.core.response.container.Container
import dev.sanmer.core.response.container.ContainerPruned
import dev.sanmer.core.response.image.Image
import dev.sanmer.core.response.image.ImagePruned
import dev.sanmer.core.response.network.Network
import dev.sanmer.core.response.network.NetworkPruned
import dev.sanmer.core.response.volume.VolumeList
import dev.sanmer.core.response.volume.VolumePruned
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.LoadData.Default.asLoadData
import dev.sanmer.docker.model.ui.home.UiContainer
import dev.sanmer.docker.model.ui.home.UiImage
import dev.sanmer.docker.model.ui.home.UiNetwork
import dev.sanmer.docker.model.ui.home.UiSystem
import dev.sanmer.docker.model.ui.home.UiVolume
import dev.sanmer.docker.repository.ClientRepository
import dev.sanmer.docker.ui.main.Screen
import io.ktor.client.call.body
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val home = savedStateHandle.toRoute<Screen.Home>()
    private val client by lazy { clientRepository.get(home.id) }

    var system by mutableStateOf<LoadData<UiSystem>>(LoadData.Loading)
        private set

    var containers by mutableStateOf<LoadData<List<UiContainer>>>(LoadData.Loading)
        private set

    var images by mutableStateOf<LoadData<List<UiImage>>>(LoadData.Loading)
        private set

    var networks by mutableStateOf<LoadData<List<UiNetwork>>>(LoadData.Loading)
        private set

    var volumes by mutableStateOf<LoadData<List<UiVolume>>>(LoadData.Loading)
        private set

    private val pruned = mutableStateMapOf<Prune, LoadData<PruneResult>>()

    init {
        Timber.d("HomeViewModel init")
        loadSystemData()
        loadContainersData()
        loadImagesData()
        loadNetworksData()
        loadVolumesData()
    }

    fun loadSystemData() {
        viewModelScope.launch {
            system = runCatching {
                UiSystem(
                    original = client.get(System.Info()).body(),
                    version = client.get(System.Version()).body()
                )
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun loadContainersData() {
        viewModelScope.launch {
            containers = runCatching {
                client.get(
                    Containers.All(all = true)
                ).body<List<Container>>().map(::UiContainer)
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun loadImagesData() {
        viewModelScope.launch {
            images = runCatching {
                client.get(
                    Images.All(all = true)
                ).body<List<Image>>().map(::UiImage)
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun loadNetworksData() {
        viewModelScope.launch {
            networks = runCatching {
                client.get(
                    Networks()
                ).body<List<Network>>().map(::UiNetwork)
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun loadVolumesData() {
        viewModelScope.launch {
            volumes = runCatching {
                client.get(
                    Volumes()
                ).body<VolumeList>().volumes.map(::UiVolume)
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun prune(target: Prune) {
        when (getPruneData(target)) {
            LoadData.Pending -> pruned[target] = LoadData.Loading
            LoadData.Loading, is LoadData.Success<*> -> return
            else -> {}
        }

        viewModelScope.launch {
            pruned[target] = runCatching {
                when (target) {
                    Prune.Containers -> client.post(
                        Containers.Prune()
                    ).body<ContainerPruned>().let(::PruneResult)

                    Prune.Images -> client.post(
                        Images.Prune()
                    ).body<ImagePruned>().let(::PruneResult)

                    Prune.Networks -> client.post(
                        Networks.Prune()
                    ).body<NetworkPruned>().let(::PruneResult)

                    Prune.Volumes -> client.post(
                        Volumes.Prune()
                    ).body<VolumePruned>().let(::PruneResult)
                }
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun getPruneData(target: Prune) = pruned.getOrDefault(target, LoadData.Pending)
    fun clearPruneData() = pruned.clear()

    data class PruneResult(
        val sizeDeleted: Int,
        val spaceReclaimed: Long
    ) {
        constructor(pruned: ContainerPruned) : this(
            sizeDeleted = pruned.containersDeleted.size,
            spaceReclaimed = pruned.spaceReclaimed
        )

        constructor(pruned: ImagePruned) : this(
            sizeDeleted = pruned.imagesDeleted.size,
            spaceReclaimed = pruned.spaceReclaimed
        )

        constructor(pruned: NetworkPruned) : this(
            sizeDeleted = pruned.networksDeleted.size,
            spaceReclaimed = 0L
        )

        constructor(pruned: VolumePruned) : this(
            sizeDeleted = pruned.volumesDeleted.size,
            spaceReclaimed = pruned.spaceReclaimed
        )
    }

    enum class Prune {
        Containers,
        Images,
        Networks,
        Volumes
    }
}