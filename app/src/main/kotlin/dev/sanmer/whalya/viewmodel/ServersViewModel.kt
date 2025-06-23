package dev.sanmer.whalya.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.core.Docker
import dev.sanmer.core.Docker.get
import dev.sanmer.core.resource.System
import dev.sanmer.core.response.system.SystemVersion
import dev.sanmer.whalya.database.entity.ServerEntity
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.repository.ClientRepository
import dev.sanmer.whalya.repository.DbRepository
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class ServersViewModel @Inject constructor(
    private val dbRepository: DbRepository,
    private val clientRepository: ClientRepository
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
            clientRepository.getOrNew(
                baseUrl = baseUrl,
                mTLS = Docker.MutualTLS(
                    caCert = caCert,
                    clientCert = clientCert,
                    clientKey = clientKey
                ),
                id = id
            ).get(System.Version()).body<SystemVersion>()
        }.onFailure {
            Timber.e(it)
        }.asLoadData()
    }
}