package dev.sanmer.whalya.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.whalya.Logger
import dev.sanmer.whalya.database.entity.ServerEntity
import dev.sanmer.whalya.repository.DbRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dbRepository: DbRepository
) : ViewModel() {
    var servers by mutableStateOf<List<ServerEntity>>(emptyList())
        private set

    private val logger = Logger.Android("SettingsViewModel")

    init {
        logger.d("init")
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