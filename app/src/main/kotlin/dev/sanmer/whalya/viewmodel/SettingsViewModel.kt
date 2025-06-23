package dev.sanmer.whalya.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.whalya.database.entity.ServerEntity
import dev.sanmer.whalya.repository.DbRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    var servers by mutableStateOf<List<ServerEntity>>(emptyList())
        private set

    init {
        Timber.d("SettingsViewModel init")
        dbObserver()
    }

    private fun dbObserver() {
        viewModelScope.launch {
            dbRepository.getServersAsFlow()
                .collect { list ->
                    servers = list.sortedBy { it.name }
                }
        }
    }
}