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
import dev.sanmer.core.Docker.delete
import dev.sanmer.core.Docker.get
import dev.sanmer.core.resource.Networks
import dev.sanmer.core.response.network.Network
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.LoadData.Default.asLoadData
import dev.sanmer.docker.model.ui.home.UiContainer.Default.shortId
import dev.sanmer.docker.model.ui.inspect.UiNetwork
import dev.sanmer.docker.repository.ClientRepository
import dev.sanmer.docker.ui.main.Screen
import io.ktor.client.call.body
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val network = savedStateHandle.toRoute<Screen.Network>()
    private val client by lazy { clientRepository.current() }

    var name by mutableStateOf(network.id.shortId())
        private set

    var data by mutableStateOf<LoadData<UiNetwork>>(LoadData.Loading)
        private set

    var bottomSheet by mutableStateOf(BottomSheet.Closed)
        private set

    var result by mutableStateOf<LoadData<Operate>>(LoadData.Pending)
        private set

    private var job = SupervisorJob()

    init {
        Timber.d("NetworkViewModel init")
        loadData()
        resultObserver()
        addCloseable { job.cancel() }
    }

    fun loadData() {
        viewModelScope.launch {
            data = runCatching {
                client.get(
                    Networks.Inspect(id = network.id)
                ).body<Network>().let(::UiNetwork).also {
                    name = it.name
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
                    Operate.Remove -> client.delete(
                        Networks.Remove(id = network.id)
                    )
                }
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
        Remove
    }
}