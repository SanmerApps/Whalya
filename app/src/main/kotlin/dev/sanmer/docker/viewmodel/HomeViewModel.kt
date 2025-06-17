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
import dev.sanmer.core.docker.query.container.ListContainersFilters
import dev.sanmer.core.docker.query.container.PruneContainersFilters
import dev.sanmer.core.docker.query.image.ListImagesFilters
import dev.sanmer.core.docker.query.image.PruneImagesFilters
import dev.sanmer.core.docker.query.network.ListNetworksFilters
import dev.sanmer.core.docker.query.network.PruneNetworksFilters
import dev.sanmer.core.docker.query.volume.ListVolumesFilters
import dev.sanmer.core.docker.query.volume.PruneVolumesFilters
import dev.sanmer.core.docker.response.container.ContainerPruned
import dev.sanmer.core.docker.response.image.ImagePruned
import dev.sanmer.core.docker.response.network.NetworkPruned
import dev.sanmer.core.docker.response.volume.VolumePruned
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.LoadData.Default.asLoadData
import dev.sanmer.docker.model.ui.home.UiContainer
import dev.sanmer.docker.model.ui.home.UiImage
import dev.sanmer.docker.model.ui.home.UiNetwork
import dev.sanmer.docker.model.ui.home.UiSystem
import dev.sanmer.docker.model.ui.home.UiVolume
import dev.sanmer.docker.repository.DockerRepository
import dev.sanmer.docker.ui.main.Screen
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dockerRepository: DockerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val home = savedStateHandle.toRoute<Screen.Home>()
    private val docker by lazy { dockerRepository.get(home.id) }

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
                    original = docker.system.info(),
                    version = docker.system.version()
                )
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun loadContainersData() {
        viewModelScope.launch {
            containers = runCatching {
                docker.containers.list(
                    all = true,
                    limit = -1,
                    filters = ListContainersFilters()
                ).map(::UiContainer)
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun loadImagesData() {
        viewModelScope.launch {
            images = runCatching {
                docker.images.list(
                    all = true,
                    filters = ListImagesFilters()
                ).map(::UiImage)
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun loadNetworksData() {
        viewModelScope.launch {
            networks = runCatching {
                docker.networks.list(
                    filters = ListNetworksFilters()
                ).map(::UiNetwork)
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun loadVolumesData() {
        viewModelScope.launch {
            volumes = runCatching {
                docker.volumes.list(
                    filters = ListVolumesFilters()
                ).volumes.map(::UiVolume)
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
                    Prune.Containers -> docker.containers.prune(
                        filters = PruneContainersFilters()
                    ).let(::PruneResult)

                    Prune.Images -> docker.images.prune(
                        filters = PruneImagesFilters()
                    ).let(::PruneResult)

                    Prune.Networks -> docker.networks.prune(
                        filters = PruneNetworksFilters()
                    ).let(::PruneResult)

                    Prune.Volumes -> docker.volumes.prune(
                        filters = PruneVolumesFilters()
                    ).let(::PruneResult)
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