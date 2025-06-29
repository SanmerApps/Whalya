package dev.sanmer.whalya.repository

import dev.sanmer.core.Docker
import dev.sanmer.whalya.database.entity.ServerEntity
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KProperty

class ClientRepositoryImpl : ClientRepository {
    private sealed interface Key {
        data class Id(val value: Long) : Key
    }

    private val clients = hashMapOf<Key, HttpClient>()

    private val _current = MutableStateFlow<ServerEntity?>(null)

    override val current: HttpClient
        get() {
            val server by this
            return getOrCreate(server)
        }

    override fun getOrCreate(server: ServerEntity) = clients.getOrPut(Key.Id(server.id)) {
        Docker.client(
            baseUrl = server.baseUrl,
            mTLS = Docker.MutualTLS(
                caCert = server.caCert,
                clientCert = server.clientCert,
                clientKey = server.clientKey
            )
        )
    }

    override fun drop(serverId: Long) = clients.remove(Key.Id(serverId))

    override operator fun getValue(thisObj: Any?, property: KProperty<*>): ServerEntity {
        return requireNotNull(_current.value) { "Uninitialized" }
    }

    override fun setValue(thisObj: Any?, property: KProperty<*>, value: ServerEntity) {
        _current.value = value
    }
}