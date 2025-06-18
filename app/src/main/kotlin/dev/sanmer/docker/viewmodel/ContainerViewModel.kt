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
import dev.sanmer.core.docker.response.container.Container
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.LoadData.Default.asLoadData
import dev.sanmer.docker.model.ui.home.UiContainer.Default.shortId
import dev.sanmer.docker.model.ui.inspect.UiContainer
import dev.sanmer.docker.repository.DockerRepository
import dev.sanmer.docker.ui.main.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ContainerViewModel @Inject constructor(
    private val dockerRepository: DockerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val container = savedStateHandle.toRoute<Screen.Container>()
    private val docker by lazy { dockerRepository.currentDocker() }

    var name by mutableStateOf(container.id.shortId())
        private set

    var data by mutableStateOf<LoadData<UiContainer>>(LoadData.Loading)
        private set

    var state by mutableStateOf(Container.State.Created)
        private set

    var bottomSheet by mutableStateOf(BottomSheet.Closed)
        private set

    var result by mutableStateOf<LoadData<Operate>>(LoadData.Loading)
        private set

    init {
        Timber.d("ContainerViewModel init")
        loadData()
        resultObserver()
    }

    fun loadData() {
        viewModelScope.launch {
            data = runCatching {
                docker.containers.inspect(
                    id = container.id,
                    size = true
                ).let(::UiContainer).also {
                    name = it.name
                    state = it.state
                }
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
                    Operate.Stop -> docker.containers.stop(
                        id = container.id,
                        signal = "",
                        t = 0
                    )

                    Operate.Start -> docker.containers.start(
                        id = container.id,
                        detachKeys = ""
                    )

                    Operate.Restart -> docker.containers.restart(
                        id = container.id,
                        signal = "",
                        t = 0
                    )

                    Operate.Pause -> docker.containers.pause(
                        id = container.id
                    )

                    Operate.Unpause -> docker.containers.unpause(
                        id = container.id
                    )

                    Operate.Remove -> docker.containers.remove(
                        id = container.id,
                        force = false
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
        Stop,
        Start,
        Restart,
        Pause,
        Unpause,
        Remove
    }
}