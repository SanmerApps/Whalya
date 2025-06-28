package dev.sanmer.whalya.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.core.response.container.Container
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.LoadData.Default.getOrThrow
import dev.sanmer.whalya.model.ui.home.UiContainer.Default.shortId
import dev.sanmer.whalya.model.ui.inspect.UiContainer
import dev.sanmer.whalya.repository.RemoteRepository
import dev.sanmer.whalya.ui.main.Screen
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ContainerViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val container = savedStateHandle.toRoute<Screen.Container>()

    var name by mutableStateOf(container.id.shortId())
        private set

    var data by mutableStateOf<LoadData<UiContainer>>(LoadData.Loading)
        private set

    var state by mutableStateOf(Container.State.Created)
        private set

    var bottomSheet by mutableStateOf(BottomSheet.Closed)
        private set

    var result by mutableStateOf<LoadData<Operate>>(LoadData.Pending)
        private set

    private var job = SupervisorJob()

    init {
        Timber.d("ContainerViewModel init")
        loadData()
        addCloseable { job.cancel() }
    }

    fun loadData() {
        viewModelScope.launch {
            data = runCatching {
                remoteRepository.inspectContainer(id = container.id)
                    .let(::UiContainer)
                    .also {
                        name = it.name
                        state = it.state
                    }
            }.onFailure {
                Timber.e(it)
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
                    Operate.Stop -> remoteRepository.stopContainer(id = container.id)
                    Operate.Start -> remoteRepository.startContainer(id = container.id)
                    Operate.Pause -> remoteRepository.pauseContainer(id = container.id)
                    Operate.Unpause -> remoteRepository.unpauseContainer(id = container.id)
                    Operate.Restart -> remoteRepository.restartContainer(id = container.id)
                    Operate.Up -> remoteRepository.upContainer(container = data.getOrThrow().original)
                    Operate.Remove -> remoteRepository.removeContainer(id = container.id)
                }
            }.onSuccess {
                if (!operate.isDestroyed) loadData()
                remoteRepository.fetchContainers()
            }.onFailure {
                Timber.e(it)
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

    enum class BottomSheet {
        Closed,
        Operate,
        Result
    }

    enum class Operate {
        Stop,
        Start,
        Pause,
        Unpause,
        Restart,
        Up,
        Remove;

        val isDestroyed inline get() = this == Up || this == Remove
    }
}