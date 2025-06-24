package dev.sanmer.whalya.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
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
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.ui.home.UiContainer
import dev.sanmer.whalya.model.ui.home.UiImage
import dev.sanmer.whalya.model.ui.home.UiNetwork
import dev.sanmer.whalya.model.ui.home.UiSystem
import dev.sanmer.whalya.model.ui.home.UiVolume
import dev.sanmer.whalya.repository.ClientRepository
import dev.sanmer.whalya.ui.main.Screen
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

    init {
        Timber.d("HomeViewModel init")
        loadData(Load.System)
        loadData(Load.Containers)
        loadData(Load.Images)
        loadData(Load.Networks)
        loadData(Load.Volumes)
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
                        original = client.get(System.Info()).body(),
                        version = client.get(System.Version()).body()
                    )
                }.onFailure {
                    Timber.e(it)
                }.asLoadData()

                Load.Containers -> containers = runCatching {
                    client.get(
                        Containers.All(all = true)
                    ).body<List<Container>>().map(::UiContainer)
                }.onFailure {
                    Timber.e(it)
                }.asLoadData()

                Load.Images -> images = runCatching {
                    client.get(
                        Images.All(all = true)
                    ).body<List<Image>>().map(::UiImage)
                }.onFailure {
                    Timber.e(it)
                }.asLoadData()

                Load.Networks -> networks = runCatching {
                    client.get(
                        Networks()
                    ).body<List<Network>>().map(::UiNetwork)
                }.onFailure {
                    Timber.e(it)
                }.asLoadData()

                Load.Volumes -> volumes = runCatching {
                    client.get(
                        Volumes()
                    ).body<VolumeList>().volumes.map(::UiVolume)
                }.onFailure {
                    Timber.e(it)
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

            val SavedStateHandle.load
                inline get() = Load.valueOf(
                    get(Load::class.java.name) ?: None.name
                )

            val NavController.load
                inline get() = currentBackStackEntry?.savedStateHandle?.load ?: None

            fun SavedStateHandle.setLoad(value: Load) {
                set(Load::class.java.name, value.name)
            }

            fun NavController.setLoad(value: Load) {
                previousBackStackEntry?.savedStateHandle?.setLoad(value)
            }
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