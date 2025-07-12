package dev.sanmer.whalya.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.sanmer.core.response.container.ContainerPruned
import dev.sanmer.core.response.image.ImagePruned
import dev.sanmer.core.response.network.NetworkPruned
import dev.sanmer.core.response.volume.VolumePruned
import dev.sanmer.whalya.Logger
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.ui.home.UiContainer
import dev.sanmer.whalya.model.ui.home.UiImage
import dev.sanmer.whalya.model.ui.home.UiNetwork
import dev.sanmer.whalya.model.ui.home.UiSystem
import dev.sanmer.whalya.model.ui.home.UiVolume
import dev.sanmer.whalya.repository.RemoteRepository
import dev.sanmer.whalya.ui.main.Screen
import kotlinx.coroutines.launch

class HomeViewModel(
    private val remoteRepository: RemoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val home = savedStateHandle.toRoute<Screen.Home>()

    val name get() = home.name

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

    private val logger = Logger.Android("HomeViewModel")

    init {
        logger.d("init")
        remoteObserver()
    }

    fun loadData(index: Int) {
        loadData(Load.All[index])
    }

    fun loadData(load: Load) {
        viewModelScope.launch {
            when (load) {
                Load.None -> {}
                Load.System -> system = runCatching {
                    UiSystem(
                        original = remoteRepository.info(),
                        version = remoteRepository.version()
                    )
                }.onFailure {
                    logger.e(it)
                }.asLoadData()

                Load.Containers -> runCatching {
                    remoteRepository.fetchContainers()
                }.onFailure {
                    containers = LoadData.Failure(it)
                    logger.e(it)
                }

                Load.Images -> runCatching {
                    remoteRepository.fetchImages()
                }.onFailure {
                    images = LoadData.Failure(it)
                    logger.e(it)
                }

                Load.Networks -> runCatching {
                    remoteRepository.fetchNetworks()
                }.onFailure {
                    networks = LoadData.Failure(it)
                    logger.e(it)
                }

                Load.Volumes -> runCatching {
                    remoteRepository.fetchVolumes()
                }.onFailure {
                    volumes = LoadData.Failure(it)
                    logger.e(it)
                }.asLoadData()
            }
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
                    Prune.Containers -> remoteRepository.pruneContainers().let {
                        if (it.containersDeleted.isNotEmpty()) remoteRepository.fetchContainers()
                        PruneResult(it)
                    }

                    Prune.Images -> remoteRepository.pruneImages().let {
                        if (it.imagesDeleted.isNotEmpty()) remoteRepository.fetchImages()
                        PruneResult(it)
                    }

                    Prune.Networks -> remoteRepository.pruneNetworks().let {
                        if (it.networksDeleted.isNotEmpty()) remoteRepository.fetchNetworks()
                        PruneResult(it)
                    }

                    Prune.Volumes -> remoteRepository.pruneVolumes().let {
                        if (it.volumesDeleted.isNotEmpty()) remoteRepository.fetchVolumes()
                        PruneResult(it)
                    }
                }
            }.onFailure {
                logger.e(it)
            }.asLoadData()
        }
    }

    fun getPruneData(target: Prune) = pruned.getOrDefault(target, LoadData.Pending)
    fun clearPruneData() = pruned.clear()

    private fun remoteObserver() {
        loadData(Load.System)

        loadData(Load.Containers)
        viewModelScope.launch {
            remoteRepository.containersFlow
                .collect { list ->
                    containers = LoadData.Success(list.map(::UiContainer))
                }
        }

        loadData(Load.Images)
        viewModelScope.launch {
            remoteRepository.imagesFlow
                .collect { list ->
                    images = LoadData.Success(list.map(::UiImage))
                }
        }

        loadData(Load.Networks)
        viewModelScope.launch {
            remoteRepository.networksFlow
                .collect { list ->
                    networks = LoadData.Success(list.map(::UiNetwork))
                }
        }

        loadData(Load.Volumes)
        viewModelScope.launch {
            remoteRepository.volumesFlow
                .collect { list ->
                    volumes = LoadData.Success(list.map(::UiVolume))
                }
        }
    }

    enum class Load {
        None,
        System,
        Containers,
        Images,
        Networks,
        Volumes;

        companion object Default {
            val All = listOf(
                System,
                Containers,
                Images,
                Networks,
                Volumes
            )
        }
    }

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