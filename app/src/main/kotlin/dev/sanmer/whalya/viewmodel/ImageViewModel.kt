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
import dev.sanmer.core.JsonCompat.encodeJson
import dev.sanmer.core.request.image.OCIPlatform
import dev.sanmer.core.resource.Containers
import dev.sanmer.core.resource.Images
import dev.sanmer.core.response.container.Container
import dev.sanmer.core.response.image.ImageHistory
import dev.sanmer.core.response.image.ImageLowLevel
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.LoadData.Default.getOrThrow
import dev.sanmer.whalya.model.ui.home.UiContainer
import dev.sanmer.whalya.model.ui.home.UiContainer.Default.shortId
import dev.sanmer.whalya.model.ui.inspect.UiImage
import dev.sanmer.whalya.repository.ClientRepository
import dev.sanmer.whalya.ui.main.Screen
import io.ktor.client.call.body
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val image = savedStateHandle.toRoute<Screen.Image>()
    private val client by lazy { clientRepository.current() }

    var name by mutableStateOf(image.id.shortId())
        private set

    var data by mutableStateOf<LoadData<UiImage>>(LoadData.Loading)
        private set

    var containers by mutableStateOf<List<UiContainer>>(emptyList())
        private set

    var histories by mutableStateOf<List<UiImage.History>>(emptyList())
        private set

    var bottomSheet by mutableStateOf(BottomSheet.Closed)
        private set

    var result by mutableStateOf<LoadData<Operate>>(LoadData.Pending)
        private set

    private var job = SupervisorJob()

    init {
        Timber.d("ImageViewModel init")
        loadData()
        dataObserver()
        resultObserver()
        addCloseable { job.cancel() }
    }

    fun loadData() {
        viewModelScope.launch {
            data = runCatching {
                client.get(
                    Images.Inspect(id = image.id)
                ).body<ImageLowLevel>().let(::UiImage).also {
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
                    Operate.Pull -> {
                        val image = data.getOrThrow().original
                        client.post(
                            Images.Create(fromImage = image.repoTags.first())
                        )
                    }

                    Operate.Remove -> client.delete(
                        Images.Remove(id = image.id)
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

    private fun dataObserver() {
        viewModelScope.launch {
            snapshotFlow { data }
                .filterIsInstance<LoadData.Success<UiImage>>()
                .collectLatest {
                    loadContainers(it.value.original)
                    loadHistories(it.value.original)
                }
        }
    }

    private fun loadContainers(image: ImageLowLevel) {
        viewModelScope.launch {
            runCatching {
                containers = client.get(
                    Containers.All(
                        filters = Containers.All.Filters(
                            ancestor = listOf(image.id)
                        ).encodeJson()
                    )
                ).body<List<Container>>()
                    .map(::UiContainer)
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    private fun loadHistories(image: ImageLowLevel) {
        viewModelScope.launch {
            runCatching {
                histories = client.get(
                    Images.History(
                        id = image.id,
                        platform = OCIPlatform(
                            architecture = image.architecture,
                            os = image.os,
                            variant = image.variant
                        ).encodeJson()
                    )
                ).body<List<ImageHistory>>().map(UiImage::History).asReversed()
            }.onFailure {
                Timber.e(it)
            }
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
        Label,
        Layer,
        Operate,
        Result
    }

    enum class Operate {
        Pull,
        Remove
    }
}