package dev.sanmer.whalya.ui.screens.licenses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.whalya.Logger
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.ui.UiLicense
import dev.sanmer.whalya.repository.LicensesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LicensesViewModel(
    private val licensesRepository: LicensesRepository
) : ViewModel() {
    var data by mutableStateOf<LoadData<List<UiLicense>>>(LoadData.Loading)
        private set

    private val logger = Logger.Android("LicensesViewModel")

    init {
        logger.d("init")
        loadData()
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
}