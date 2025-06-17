package dev.sanmer.docker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.core.docker.query.container.LogsContainerTail
import dev.sanmer.core.docker.response.container.ContainerLog
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.LoadData.Default.asLoadData
import dev.sanmer.docker.repository.DockerRepository
import dev.sanmer.docker.ui.main.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ContainerLogsViewModel @Inject constructor(
    private val dockerRepository: DockerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val containerLogs = savedStateHandle.toRoute<Screen.ContainerLogs>()
    private val docker by lazy { dockerRepository.currentDocker() }

    var data by mutableStateOf<LoadData<List<ContainerLog>>>(LoadData.Loading)
        private set

    var isRunning by mutableStateOf(true)
        private set

    init {
        Timber.d("ContainerLogsViewModel init")
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            while (isActive && isRunning) {
                data = runCatching {
                    docker.containers.logs(
                        id = containerLogs.id,
                        stdout = true,
                        stderr = true,
                        tail = LogsContainerTail.Number(1000)
                    ).asReversed()
                }.onFailure {
                    isRunning = false
                    Timber.e(it)
                }.asLoadData()

                delay(1000.milliseconds)
            }
        }
    }

    fun update(block: (Boolean) -> Boolean) {
        isRunning = block(isRunning)
        loadData()
    }
}