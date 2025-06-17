package dev.sanmer.docker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.core.Auth
import dev.sanmer.docker.database.entity.ServerEntity
import dev.sanmer.docker.model.LoadData
import dev.sanmer.docker.model.LoadData.Default.asLoadData
import dev.sanmer.docker.repository.DbRepository
import dev.sanmer.docker.repository.DockerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ServersViewModel @Inject constructor(
    private val dbRepository: DbRepository,
    private val dockerRepository: DockerRepository
) : ViewModel() {
    var data by mutableStateOf<LoadData<List<ServerEntity>>>(LoadData.Loading)
        private set

    private val pings = mutableStateMapOf<Long, Boolean>()

    init {
        Timber.d("ServersViewModel init")
        dbObserver()
    }

    fun ping(server: ServerEntity): Boolean {
        viewModelScope.launch {
            if (pings[server.id] == true) return@launch
            val version = server.version().apply { pings[server.id] = isSuccess }
            if (version is LoadData.Success) {
                dbRepository.updateServer(
                    server.copy(
                        version = version.value.version,
                        os = version.value.os,
                        arch = version.value.arch
                    )
                )
            }
        }
        return pings.getOrDefault(server.id, false)
    }

    private fun dbObserver() {
        viewModelScope.launch {
            dbRepository.getServersAsFlow()
                .collect { list ->
                    data = LoadData.Success(list.sortedBy { it.name })
                }
        }
    }

    private suspend fun ServerEntity.version() = withContext(Dispatchers.IO) {
        runCatching {
            dockerRepository.getOrNew(
                baseUrl = baseUrl,
                auth = Auth.MutualTLS(
                    caCert = caCert,
                    clientCert = clientCert,
                    clientKey = clientKey
                ),
                id = id
            ).system.version()
        }.onFailure {
            Timber.e(it)
        }.asLoadData()
    }
}