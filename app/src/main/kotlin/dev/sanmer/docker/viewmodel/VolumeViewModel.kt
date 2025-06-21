package dev.sanmer.docker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.core.Docker.delete
import dev.sanmer.core.Docker.get
import dev.sanmer.core.JsonCompat.encodeJson
import dev.sanmer.core.resource.Containers
import dev.sanmer.core.resource.Volumes
import dev.sanmer.core.response.container.Container
import dev.sanmer.core.response.volume.Volume
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.LoadData.Default.asLoadData
import dev.sanmer.docker.model.ui.home.UiContainer
import dev.sanmer.docker.model.ui.inspect.UiVolume
import dev.sanmer.docker.repository.ClientRepository
import dev.sanmer.docker.ui.main.Screen
import io.ktor.client.call.body
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class VolumeViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val volume = savedStateHandle.toRoute<Screen.Volume>()
    private val client by lazy { clientRepository.current() }

    val name get() = volume.name

    var data by mutableStateOf<LoadData<UiVolume>>(LoadData.Loading)
        private set

    var containers by mutableStateOf<List<UiContainer>>(emptyList())
        private set

    var bottomSheet by mutableStateOf(BottomSheet.Closed)
        private set

    var result by mutableStateOf<LoadData<Operate>>(LoadData.Loading)
        private set

    init {
        Timber.d("VolumeViewModel init")
        loadData()
        dataObserver()
        resultObserver()
    }

    fun loadData() {
        viewModelScope.launch {
            data = runCatching {
                client.get(
                    Volumes.Inspect(name = volume.name)
                ).body<Volume>().let(::UiVolume)
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun operate(operate: Operate) {
        viewModelScope.launch {
            bottomSheet = BottomSheet.Result
            result = runCatching {
                when (operate) {
                    Operate.Remove -> client.delete(
                        Volumes.Remove(name = volume.name)
                    )
                }
            }.onFailure {
                Timber.e(it)
            }.asLoadData { operate }
        }
    }

    fun update(value: BottomSheet) {
        bottomSheet = value
        if (bottomSheet == BottomSheet.Operate) {
            result = LoadData.Pending
        }
    }

    private fun dataObserver() {
        viewModelScope.launch {
            snapshotFlow { data }
                .filterIsInstance<LoadData.Success<UiVolume>>()
                .collectLatest {
                    loadContainers(it.value.original)
                }
        }
    }

    private fun loadContainers(volume: Volume) {
        viewModelScope.launch {
            runCatching {
                containers = client.get(
                    Containers.All(
                        filters = Containers.All.Filters(
                            volume = listOf(volume.name)
                        ).encodeJson()
                    )
                ).body<List<Container>>()
                    .map(::UiContainer)
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    private fun resultObserver() {
        viewModelScope.launch {
            snapshotFlow { result }
                .filterIsInstance<LoadData.Success<*>>()
                .collectLatest {
                    loadData()
                }
        }
    }

    enum class BottomSheet {
        Closed,
        Operate,
        Result
    }

    enum class Operate {
        Remove
    }
}