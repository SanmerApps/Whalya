package dev.sanmer.docker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.LoadData.Default.asLoadData
import dev.sanmer.docker.model.ui.home.UiContainer.Default.shortId
import dev.sanmer.docker.model.ui.inspect.UiNetwork
import dev.sanmer.docker.repository.DockerRepository
import dev.sanmer.docker.ui.main.Screen
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val dockerRepository: DockerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val network = savedStateHandle.toRoute<Screen.Network>()
    private val docker by lazy { dockerRepository.currentDocker() }

    var name by mutableStateOf(network.id.shortId())
        private set

    var data by mutableStateOf<LoadData<UiNetwork>>(LoadData.Loading)
        private set

    var bottomSheet by mutableStateOf(BottomSheet.Closed)
        private set

    var result by mutableStateOf<LoadData<Unit>>(LoadData.Loading)
        private set

    init {
        Timber.d("NetworkViewModel init")
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            data = runCatching {
                docker.networks.inspect(
                    id = network.id,
                    scope = ""
                ).also {
                    name = it.name
                }.let(::UiNetwork)
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun remove() {
        viewModelScope.launch {
            bottomSheet = BottomSheet.Result
            result = runCatching {
                docker.networks.remove(
                    id = network.id
                )
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun update(value: BottomSheet) {
        bottomSheet = value
        if (bottomSheet == BottomSheet.Operate) {
            result = LoadData.Pending
        }
    }

    enum class BottomSheet {
        Closed,
        Operate,
        Result
    }
}