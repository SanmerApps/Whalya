package dev.sanmer.whalya.viewmodel

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
import dev.sanmer.core.Docker.post
import dev.sanmer.core.converter.toConfig
import dev.sanmer.core.resource.Containers
import dev.sanmer.core.response.container.Container
import dev.sanmer.core.response.container.ContainerLowLevel
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.LoadData.Default.getOrThrow
import dev.sanmer.whalya.model.ui.home.UiContainer.Default.name
import dev.sanmer.whalya.model.ui.home.UiContainer.Default.shortId
import dev.sanmer.whalya.model.ui.inspect.UiContainer
import dev.sanmer.whalya.repository.ClientRepository
import dev.sanmer.whalya.ui.main.Screen
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ContainerViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val container = savedStateHandle.toRoute<Screen.Container>()
    private val client by lazy { clientRepository.current() }

    var name by mutableStateOf(container.id.shortId())
        private set

    var data by mutableStateOf<LoadData<UiContainer>>(LoadData.Loading)
        private set

    var state by mutableStateOf(Container.State.Created)
        private set

    var bottomSheet by mutableStateOf(BottomSheet.Closed)
        private set

    var result by mutableStateOf<LoadData<Operate>>(LoadData.Pending)
        private set

    private var job = SupervisorJob()

    init {
        Timber.d("ContainerViewModel init")
        loadData()
        resultObserver()
        addCloseable { job.cancel() }
    }

    fun loadData() {
        viewModelScope.launch {
            data = runCatching {
                client.get(
                    Containers.Inspect(
                        id = container.id,
                        size = true
                    )
                ).body<ContainerLowLevel>().let(::UiContainer).also {
                    name = it.name
                    state = it.state
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
                    Operate.Stop -> client.post(
                        Containers.Stop(id = container.id)
                    )

                    Operate.Start -> client.post(
                        Containers.Start(id = container.id)
                    )

                    Operate.Pause -> client.post(
                        Containers.Pause(id = container.id)
                    )

                    Operate.Unpause -> client.post(
                        Containers.Unpause(id = container.id)
                    )

                    Operate.Restart -> client.post(
                        Containers.Restart(id = container.id)
                    )

                    Operate.Up -> {
                        val container = data.getOrThrow().original
                        val config = container.toConfig()
                        client.post(
                            Containers.Stop(id = container.id)
                        )
                        client.delete(
                            Containers.Remove(id = container.id)
                        )
                        client.post(
                            Containers.Create(name = container.name)
                        ) {
                            contentType(ContentType.Application.Json)
                            setBody(config)
                        }
                        client.post(
                            Containers.Start(id = container.name.name())
                        )
                    }

                    Operate.Remove -> client.delete(
                        Containers.Remove(id = container.id)
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
        Stop,
        Start,
        Pause,
        Unpause,
        Restart,
        Up,
        Remove
    }
}