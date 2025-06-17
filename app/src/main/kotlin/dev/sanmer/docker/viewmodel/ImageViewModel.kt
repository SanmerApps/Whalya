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
import dev.sanmer.core.docker.query.container.ListContainersFilters
import dev.sanmer.core.docker.query.image.OCIPlatform
import dev.sanmer.core.docker.response.image.ImageLowLevel
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.LoadData.Default.asLoadData
import dev.sanmer.docker.model.ui.home.UiContainer
import dev.sanmer.docker.model.ui.home.UiContainer.Default.shortId
import dev.sanmer.docker.model.ui.inspect.UiImage
import dev.sanmer.docker.repository.DockerRepository
import dev.sanmer.docker.ui.main.Screen
import dev.sanmer.docker.viewmodel.VolumeViewModel.BottomSheet
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val dockerRepository: DockerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val image = savedStateHandle.toRoute<Screen.Image>()
    private val docker by lazy { dockerRepository.currentDocker() }

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

    var result by mutableStateOf<LoadData<Unit>>(LoadData.Loading)
        private set

    init {
        Timber.d("ImageViewModel init")
        loadData()
        dbObserver()
    }

    fun loadData() {
        viewModelScope.launch {
            data = runCatching {
                docker.images.inspect(
                    id = image.id
                ).let(::UiImage).also {
                    name = it.name
                }
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }

    fun remove() {
        viewModelScope.launch {
            bottomSheet = BottomSheet.Result
            result = runCatching {
                docker.images.remove(
                    id = image.id,
                    force = false,
                    noprune = false
                ).let {}
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

    private fun dbObserver() {
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
                containers = docker.containers.list(
                    limit = -1,
                    filters = ListContainersFilters(
                        ancestor = listOf(image.id)
                    )
                ).map(::UiContainer)
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    private fun loadHistories(image: ImageLowLevel) {
        viewModelScope.launch {
            runCatching {
                histories = docker.images.history(
                    id = image.id,
                    platform = OCIPlatform(
                        architecture = image.architecture,
                        os = image.os,
                        variant = image.variant
                    )
                ).map(UiImage::History).asReversed()
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    enum class BottomSheet {
        Closed,
        Layer,
        Operate,
        Result
    }
}