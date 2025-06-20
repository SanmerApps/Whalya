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
import dev.sanmer.core.response.container.ContainerStats
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.LoadData.Default.asLoadData
import dev.sanmer.docker.model.ui.inspect.UiContainerStats
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
class ContainerStatsViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val containerStats = savedStateHandle.toRoute<Screen.ContainerStats>()
    private val client by lazy { clientRepository.current() }

    var data by mutableStateOf<LoadData<UiContainerStats>>(LoadData.Loading)
        private set

    var isRunning by mutableStateOf(true)
        private set

    init {
        Timber.d("ContainerStatsViewModel init")
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            while (isActive && isRunning) {
                data = runCatching {
                    client.get(
                        Containers.Stats(
                            id = containerStats.id,
                            stream = false,
                            oneShot = false
                        )
                    ).body<ContainerStats>().let(::UiContainerStats)
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