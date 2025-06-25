package dev.sanmer.whalya.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.sanmer.whalya.Const
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.license.Artifact
import dev.sanmer.whalya.model.ui.UiLicense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LicenseViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    var data by mutableStateOf<LoadData<List<UiLicense>>>(LoadData.Loading)
        private set

    init {
        Timber.d("LicenseViewModel init")
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            data = runCatching {
                context.assets.open(Const.LICENSEE_PATH).use { stream ->
                    Json.decodeFromStream<List<Artifact>>(stream)
                        .map(::UiLicense)
                }
            }.onFailure {
                Timber.e(it)
            }.asLoadData()
        }
    }
}