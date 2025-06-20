package dev.sanmer.docker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.core.Docker.get
import dev.sanmer.core.resource.Containers
import dev.sanmer.core.response.container.ContainerLog
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.LoadData.Default.asLoadData
import dev.sanmer.docker.repository.ClientRepository
import dev.sanmer.docker.ui.main.Screen
import io.ktor.client.call.body
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ContainerLogsViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val containerLogs = savedStateHandle.toRoute<Screen.ContainerLogs>()
    private val client by lazy { clientRepository.current() }

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
                    client.get(
                        Containers.Logs(
                            id = containerLogs.id,
                            follow = false,
                            stdout = true,
                            stderr = true
                        )
                    ).body<List<ContainerLog>>().asReversed()
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
        if (isRunning) {
            loadData()
        }
    }
}