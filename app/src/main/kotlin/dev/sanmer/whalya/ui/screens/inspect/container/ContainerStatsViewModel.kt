package dev.sanmer.whalya.ui.screens.inspect.container

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
import dev.sanmer.whalya.model.ui.inspect.UiContainerStats
import dev.sanmer.whalya.observer.ForegroundObserver
import dev.sanmer.whalya.repository.RemoteRepository
import dev.sanmer.whalya.ui.main.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ContainerStatsViewModel(
    private val remoteRepository: RemoteRepository,
    private val foregroundObserver: ForegroundObserver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val containerStats = savedStateHandle.toRoute<Screen.ContainerStats>()

    var data by mutableStateOf<LoadData<UiContainerStats>>(LoadData.Loading)
        private set

    var isRunning by mutableStateOf(false)
        private set

    private val logger = Logger.Android("ContainerStatsViewModel")

    init {
        logger.d("init")
        foregroundObserver()
    }

    private fun loadData() {
        viewModelScope.launch {
            while (isActive && isRunning) {
                data = runCatching {
                    remoteRepository.containersStats(id = containerStats.id)
                        .let(::UiContainerStats)
                }.onFailure {
                    isRunning = false
                    logger.e(it)
                }.asLoadData()

                delay(1000.milliseconds)
            }
        }
    }

    fun toggleRunning() {
        isRunning = !isRunning
        if (isRunning) {
            loadData()
        }
    }


    private fun foregroundObserver() {
        viewModelScope.launch {
            foregroundObserver.isForeground
                .collectLatest {
                    logger.d("isForeground = $it")
                    toggleRunning()
                }
        }
    }
}