package dev.sanmer.whalya.ui.screens.servers

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.sanmer.core.Docker
import dev.sanmer.core.Docker.get
import dev.sanmer.core.resource.System
import dev.sanmer.core.response.system.SystemVersion
import dev.sanmer.whalya.Const
import dev.sanmer.whalya.Logger
import dev.sanmer.whalya.R
import dev.sanmer.whalya.database.entity.ServerEntity
import dev.sanmer.whalya.model.LoadData
import dev.sanmer.whalya.model.LoadData.Default.asLoadData
import dev.sanmer.whalya.model.LoadData.Default.getOrThrow
import dev.sanmer.whalya.observer.NetworkObserver
import dev.sanmer.whalya.repository.ClientRepository
import dev.sanmer.whalya.repository.DbRepository
import dev.sanmer.whalya.ui.main.Screen
import io.ktor.client.call.body
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddServerViewModel(
    private val dbRepository: DbRepository,
    private val clientRepository: ClientRepository,
    private val networkObserver: NetworkObserver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val addServer = savedStateHandle.toRoute<Screen.AddServer>()
    val isEdit get() = addServer.isEdit

    var input by mutableStateOf(Input())
        private set

    val cert: String
        inline get() = when (input.cert) {
            MutualTLS.CA -> input.caCert
            MutualTLS.Cert -> input.clientCert
            MutualTLS.Key -> input.clientKey
        }

    var data by mutableStateOf<LoadData<SystemVersion>>(LoadData.Loading)
        private set

    var control by mutableStateOf(Control.Edit)
        private set

    private val logger = Logger.Android("AddServerViewModel")

    init {
        logger.d("init")
        networkObserver()
        dbObserver()
    }

    fun input(block: (Input) -> Input) {
        input = block(input)
    }

    fun inputCert(value: String) {
        when (input.cert) {
            MutualTLS.CA -> input { it.copy(caCert = value) }
            MutualTLS.Cert -> input { it.copy(clientCert = value) }
            MutualTLS.Key -> input { it.copy(clientKey = value) }
        }
    }

    fun update(value: Control) {
        control = value
    }

    fun connect() {
        viewModelScope.launch {
            control = Control.Connecting
            data = runCatching {
                Docker.client(
                    baseUrl = input.apiEndpoint,
                    mTLS = Docker.MutualTLS(
                        caCert = input.caCert,
                        clientCert = input.clientCert,
                        clientKey = input.clientKey
                    )
                ).get(System.Version()).body<SystemVersion>()
            }.onSuccess {
                control = Control.Connected
            }.onFailure {
                control = Control.Closed
                logger.e(it)
            }.asLoadData()
        }
    }

    fun save() {
        viewModelScope.launch {
            val version = data.getOrThrow()
            val value = ServerEntity(
                id = if (isEdit) addServer.id else 0,
                baseUrl = input.apiEndpoint,
                caCert = input.caCert,
                clientCert = input.clientCert,
                clientKey = input.clientKey,
                name = input.name.ifEmpty { version.platform.name },
                version = version.version,
                os = version.os,
                arch = version.arch
            )

            runCatching {
                if (isEdit) {
                    dbRepository.updateServer(value)
                } else {
                    dbRepository.insertServer(value)
                }
            }.onSuccess {
                control = Control.Saved
            }.onFailure {
                logger.e(it)
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            runCatching {
                clientRepository.drop(addServer.id)
                dbRepository.deleteServerById(addServer.id)
            }.onSuccess {
                control = Control.Saved
            }.onFailure {
                logger.e(it)
            }
        }
    }

    private fun networkObserver() {
        viewModelScope.launch {
            networkObserver.state
                .collectLatest {
                    control = if (!it.isAvailable) Control.NetworkUnavailable else Control.Edit
                }
        }
    }

    private fun dbObserver() {
        viewModelScope.launch {
            dbRepository.getServerByIdAsFlow(addServer.id)
                .collect { entity ->
                    input {
                        it.copy(
                            name = entity.name,
                            apiEndpoint = entity.baseUrl,
                            caCert = entity.caCert,
                            clientCert = entity.clientCert,
                            clientKey = entity.clientKey
                        )
                    }
                }
        }
    }

    data class Input(
        val cert: MutualTLS = MutualTLS.CA,
        val name: String = "",
        val apiEndpoint: String = "",
        val caCert: String = "",
        val clientCert: String = "",
        val clientKey: String = ""
    )

    enum class MutualTLS(
        @field:StringRes val text: Int,
        val placeholder: String
    ) {
        CA(R.string.sever_ca_cert, Const.CERT_DEMO),
        Cert(R.string.sever_client_cert, Const.CERT_DEMO),
        Key(R.string.sever_client_key, Const.KEY_DEMO)
    }

    enum class Control {
        NetworkUnavailable,
        Edit,
        Connecting,
        Closed,
        Connected,
        Saved;

        val isNetworkUnavailable inline get() = this == NetworkUnavailable
        val isEdit inline get() = this == Edit
        val isConnecting inline get() = this == Connecting
        val isSaved inline get() = this == Saved
        val isNoEdit inline get() = this == Closed || this == Connected || this == Saved
    }
}