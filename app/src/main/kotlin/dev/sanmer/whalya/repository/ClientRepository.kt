package dev.sanmer.whalya.repository

import dev.sanmer.core.Docker
import dev.sanmer.whalya.database.entity.ServerEntity
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KProperty

@Singleton
class ClientRepository @Inject constructor() {
    private sealed interface Key {
        data class Id(val value: Long) : Key
    }

    private val _current = MutableStateFlow<ServerEntity?>(null)
    private val clients = hashMapOf<Key, HttpClient>()

    fun set(server: ServerEntity) {
        _current.value = server
    }

    fun drop(id: Long) = clients.remove(Key.Id(id))

    fun getOrNew(server: ServerEntity) = clients.getOrPut(Key.Id(server.id)) {
        Docker.client(
            baseUrl = server.baseUrl,
            mTLS = Docker.MutualTLS(
                caCert = server.caCert,
                clientCert = server.clientCert,
                clientKey = server.clientKey
            )
        )
    }

    operator fun getValue(thisObj: Any?, property: KProperty<*>): HttpClient {
        return getOrNew(requireNotNull(_current.value) { "No Server" })
    }
}