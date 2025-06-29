package dev.sanmer.whalya.ui.screens.inspect.container

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.sanmer.core.response.container.ContainerLog
import dev.sanmer.whalya.Logger
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.LoadData.Default.getValue
import dev.sanmer.whalya.repository.RemoteRepository
import dev.sanmer.whalya.ui.main.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ContainerLogsViewModel(
    private val remoteRepository: RemoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val containerLogs = savedStateHandle.toRoute<Screen.ContainerLogs>()

    var data by mutableStateOf<LoadData<List<ContainerLog>>>(LoadData.Loading)
        private set

    var isRunning by mutableStateOf(true)
        private set

    var isSearch by mutableStateOf(false)
        private set

    private var cache = emptyList<ContainerLog>()
    private val keyFlow = MutableStateFlow("")

    private val logger = Logger.Android("ContainerLogsViewModel")

    init {
        logger.d("init")
        loadData()
        keyObserver()
    }

    fun toggleRunning() {
        isRunning = !isRunning && !isSearch
        if (isRunning) {
            loadData()
        }
    }

    fun toggleSearch() {
        isSearch = !isSearch
        cache = if (isSearch) {
            isRunning = false
            data.getValue(emptyList()) { it }
        } else {
            toggleRunning()
            emptyList()
        }
    }

    fun search(value: String) {
        keyFlow.value = value
    }

    private fun loadData() {
        viewModelScope.launch {
            while (isActive && isRunning) {
                data = runCatching {
                    remoteRepository.containerLogs(id = containerLogs.id)
                        .asReversed()
                }.onFailure {
                    logger.e(it)
                }.asLoadData()

                delay(1000.milliseconds)
            }
        }
    }

    private fun keyObserver() {
        viewModelScope.launch {
            keyFlow.collectLatest { key ->
                if (isSearch) {
                    data = LoadData.Success(
                        cache.filter { it.content.contains(key, ignoreCase = true) }
                    )
                }
            }
        }
    }
}