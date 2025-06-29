package dev.sanmer.whalya.ui.screens.inspect.image

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.sanmer.core.response.image.ImageLowLevel
import dev.sanmer.whalya.Logger
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.LoadData.Default.getOrThrow
import dev.sanmer.whalya.model.ui.home.UiContainer
import dev.sanmer.whalya.model.ui.home.UiContainer.Default.shortId
import dev.sanmer.whalya.model.ui.inspect.UiImage
import dev.sanmer.whalya.repository.RemoteRepository
import dev.sanmer.whalya.ui.main.Screen
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ImageViewModel(
    private val remoteRepository: RemoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val image = savedStateHandle.toRoute<Screen.Image>()

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

    private val logger = Logger.Android("ImageViewModel")

    init {
        logger.d("init")
        loadData()
        containersObserver()
        addCloseable { job.cancel() }
    }

    fun loadData() {
        viewModelScope.launch {
            data = runCatching {
                remoteRepository.inspectImage(id = image.id)
                    .let(::UiImage)
                    .also {
                        name = it.name
                    }
            }.onSuccess {
                loadHistories(it.original)
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
                    Operate.Pull -> remoteRepository.pullImage(image = data.getOrThrow().original)
                    Operate.Remove -> remoteRepository.removeImage(id = image.id)
                }
            }.onSuccess {
                if (!operate.isDestroyed) loadData()
                remoteRepository.fetchImages()
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

    private suspend fun loadHistories(image: ImageLowLevel) {
        runCatching {
            histories = remoteRepository.fetchHistories(image = image)
                .map(UiImage::History)
                .asReversed()
        }.onFailure {
            logger.e(it)
        }
    }

    private fun containersObserver() {
        viewModelScope.launch {
            remoteRepository.containersFlow
                .collectLatest { list ->
                    containers = list.filter { container ->
                        container.imageId.contains(image.id)
                    }.map(::UiContainer)
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
        Remove;

        val isDestroyed inline get() = this == Remove
    }
}