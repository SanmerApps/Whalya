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
import dev.sanmer.core.docker.query.container.ListContainersFilters
import dev.sanmer.core.docker.response.volume.Volume
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.LoadData.Default.asLoadData
import dev.sanmer.docker.model.ui.home.UiContainer
import dev.sanmer.docker.model.ui.inspect.UiVolume
import dev.sanmer.docker.repository.DockerRepository
import dev.sanmer.docker.ui.main.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class VolumeViewModel @Inject constructor(
    private val dockerRepository: DockerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val volume = savedStateHandle.toRoute<Screen.Volume>()
    private val docker by lazy { dockerRepository.currentDocker() }

    val name get() = volume.name

    var data by mutableStateOf<LoadData<UiVolume>>(LoadData.Loading)
        private set

    var containers by mutableStateOf<List<UiContainer>>(emptyList())
        private set

    var bottomSheet by mutableStateOf(BottomSheet.Closed)
        private set

    var result by mutableStateOf<LoadData<Unit>>(LoadData.Loading)
        private set

    init {
        Timber.d("VolumeViewModel init")
        loadData()
        dataObserver()
    }

    fun loadData() {
        viewModelScope.launch {
            data = runCatching {
                docker.volumes.inspect(
                    name = volume.name
                ).let(::UiVolume)
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun remove() {
        viewModelScope.launch {
            bottomSheet = BottomSheet.Result
            result = runCatching {
                docker.volumes.remove(
                    name = volume.name,
                    force = false
                )
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
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
                containers = docker.containers.list(
                    limit = -1,
                    filters = ListContainersFilters(
                        volume = listOf(volume.name)
                    )
                ).map(::UiContainer)
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    enum class BottomSheet {
        Closed,
        Operate,
        Result
    }
}