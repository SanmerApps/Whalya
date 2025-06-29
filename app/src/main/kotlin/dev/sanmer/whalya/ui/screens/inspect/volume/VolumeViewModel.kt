package dev.sanmer.whalya.ui.screens.inspect.volume

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.sanmer.whalya.Logger
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.ui.home.UiContainer
import dev.sanmer.whalya.model.ui.inspect.UiVolume
import dev.sanmer.whalya.repository.RemoteRepository
import dev.sanmer.whalya.ui.main.Screen
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VolumeViewModel(
    private val remoteRepository: RemoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val volume = savedStateHandle.toRoute<Screen.Volume>()

    val name get() = volume.name

    var data by mutableStateOf<LoadData<UiVolume>>(LoadData.Loading)
        private set

    var containers by mutableStateOf<List<UiContainer>>(emptyList())
        private set

    var bottomSheet by mutableStateOf(BottomSheet.Closed)
        private set

    var result by mutableStateOf<LoadData<Operate>>(LoadData.Pending)
        private set

    private var job = SupervisorJob()

    private val logger = Logger.Android("VolumeViewModel")

    init {
        logger.d("init")
        loadData()
        containersObserver()
        addCloseable { job.cancel() }
    }

    fun loadData() {
        viewModelScope.launch {
            data = runCatching {
                remoteRepository.inspectVolume(name = volume.name)
                    .let(::UiVolume)
            }.onFailure {
                logger.e(it)
            }.asLoadData()
        }
    }

    fun operate(operate: Operate) {
        job = SupervisorJob()
        viewModelScope.launch(job) {
            bottomSheet = BottomSheet.Result
            result = LoadData.Loading
            result = runCatching {
                when (operate) {
                    Operate.Remove -> remoteRepository.removeVolume(name = volume.name)
                }
            }.onSuccess {
                if (!operate.isDestroyed) loadData()
                remoteRepository.fetchVolumes()
            }.onFailure {
                logger.e(it)
            }.asLoadData { operate }
        }
    }

    fun update(value: BottomSheet) {
        val preValue = bottomSheet
        bottomSheet = value
        if (preValue == BottomSheet.Result) {
            result = LoadData.Pending
            job.cancel()
        }
    }

    private fun containersObserver() {
        viewModelScope.launch {
            remoteRepository.containersFlow
                .collectLatest { list ->
                    containers = list.filter { container ->
                        container.mounts.any { it.name == volume.name }
                    }.map(::UiContainer)
                }
        }
    }

    enum class BottomSheet {
        Closed,
        Operate,
        Result
    }

    enum class Operate {
        Remove;

        val isDestroyed inline get() = this == Remove
    }
}