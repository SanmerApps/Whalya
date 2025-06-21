package dev.sanmer.docker.repository

import dev.sanmer.core.Docker
import io.ktor.client.HttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientRepository @Inject constructor() {
    private sealed class Key {
        data class Id(val value: Long) : Key()
    }

    private val clients = hashMapOf<Key, HttpClient>()
    private var currentId = -1L

    fun getOrNew(
        baseUrl: String,
        mTLS: Docker.MutualTLS,
        id: Long
    ) = clients.getOrPut(Key.Id(id)) {
        Docker.client(
            baseUrl = baseUrl,
            mTLS = mTLS
        )
    }

    fun get(id: Long) = clients.getValue(Key.Id(id)).also { currentId = id }
    fun drop(id: Long) = clients.remove(Key.Id(id))
    fun current() = clients.getValue(Key.Id(currentId))
}