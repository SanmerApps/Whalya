package dev.sanmer.docker.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import dev.sanmer.core.Auth
import dev.sanmer.core.docker.Docker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DockerRepository @Inject constructor() {
    private sealed class Key {
        data class Id(val value: Long) : Key()
    }

    private val docker = hashMapOf<Key, Docker>()
    var currentId by mutableLongStateOf(-1)
        private set

    fun new(
        baseUrl: String,
        auth: Auth
    ) = Docker(
        baseUrl = baseUrl,
        auth = auth
    )

    fun getOrNew(
        baseUrl: String,
        auth: Auth,
        id: Long
    ) = docker.getOrPut(Key.Id(id)) {
        Docker(
            baseUrl = baseUrl,
            auth = auth
        )
    }

    fun get(id: Long) = docker.getValue(Key.Id(id)).also { currentId = id }
    fun drop(id: Long) = docker.remove(Key.Id(id))
    fun currentDocker() = docker.getValue(Key.Id(currentId))
}