package dev.sanmer.whalya.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.ui.inspect.UiContainerStats
import dev.sanmer.whalya.repository.RemoteRepository
import dev.sanmer.whalya.ui.main.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ContainerStatsViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val containerStats = savedStateHandle.toRoute<Screen.ContainerStats>()

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
                    remoteRepository.containersStats(id = containerStats.id)
                        .let(::UiContainerStats)
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