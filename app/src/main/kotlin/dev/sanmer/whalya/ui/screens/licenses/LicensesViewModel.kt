package dev.sanmer.whalya.ui.screens.licenses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.whalya.Logger
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.LoadData.Default.getValue
import dev.sanmer.whalya.model.ui.UiLicense
import dev.sanmer.whalya.repository.LicensesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LicensesViewModel(
    private val licensesRepository: LicensesRepository
) : ViewModel() {
    var data by mutableStateOf<LoadData<List<UiLicense>>>(LoadData.Loading)
        private set

    var isSearch by mutableStateOf(false)
        private set

    private var cache = emptyList<UiLicense>()
    private val keyFlow = MutableStateFlow("")

    private val logger = Logger.Android("LicensesViewModel")

    init {
        logger.d("init")
        loadData()
        keyObserver()
    }

    fun toggleSearch() {
        isSearch = !isSearch
        cache = if (isSearch) {
            data.getValue(emptyList()) { it }
        } else {
            search("")
            emptyList()
        }
    }

    fun search(value: String) {
        keyFlow.value = value
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            data = runCatching {
                licensesRepository.fetch().map(::UiLicense)
            }.onFailure {
                logger.e(it)
            }.asLoadData()
        }
    }

    private fun keyObserver() {
        viewModelScope.launch {
            keyFlow.collectLatest { key ->
                data = LoadData.Success(
                    cache.filter {
                        it.original.name.contains(
                            key, ignoreCase = true
                        ) || it.original.groupId.contains(
                            key, ignoreCase = true
                        ) || it.original.artifactId.contains(
                            key, ignoreCase = true
                        )
                    }
                )
            }
        }
    }
}