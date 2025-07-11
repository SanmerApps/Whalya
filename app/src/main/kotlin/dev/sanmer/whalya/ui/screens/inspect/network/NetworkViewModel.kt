package dev.sanmer.whalya.ui.screens.inspect.network

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
import dev.sanmer.whalya.model.ui.home.UiContainer.Default.shortId
import dev.sanmer.whalya.model.ui.inspect.UiNetwork
import dev.sanmer.whalya.repository.RemoteRepository
import dev.sanmer.whalya.ui.main.Screen
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NetworkViewModel(
    private val remoteRepository: RemoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val network = savedStateHandle.toRoute<Screen.Network>()

    var name by mutableStateOf(network.id.shortId())
        private set

    var data by mutableStateOf<LoadData<UiNetwork>>(LoadData.Loading)
        private set

    var bottomSheet by mutableStateOf(BottomSheet.Closed)
        private set

    var result by mutableStateOf<LoadData<Operate>>(LoadData.Pending)
        private set

    private var job = SupervisorJob()

    private val logger = Logger.Android("NetworkViewModel")

    init {
        logger.d("init")
        loadData()
        addCloseable { job.cancel() }
    }

    fun loadData() {
        viewModelScope.launch {
            data = runCatching {
                remoteRepository.inspectNetwork(id = network.id)
                    .let(::UiNetwork)
                    .also {
                        name = it.name
                    }
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
                    Operate.Remove -> remoteRepository.removeNetwork(id = network.id)
                }
            }.onSuccess {
                if (!operate.isDestroyed) loadData()
                remoteRepository.fetchNetworks()
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