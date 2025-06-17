package dev.sanmer.docker.repository

import dev.sanmer.docker.database.dao.ServerDao
import dev.sanmer.docker.database.entity.ServerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbRepository @Inject constructor(
    private val server: ServerDao
) {
    fun getServersAsFlow() = server.getAllAsFlow()

    fun getServerByIdAsFlow(id: Long) = server.getByIdAsFlow(id).filterNotNull()

    suspend fun getServers() = withContext(Dispatchers.IO) {
        server.getAll()
    }

    suspend fun insertServer(entity: ServerEntity) = withContext(Dispatchers.IO) {
        server.insert(entity)
    }

    suspend fun updateServer(entity: ServerEntity) = withContext(Dispatchers.IO) {
        server.update(entity)
    }

    suspend fun deleteServer(entity: ServerEntity) = withContext(Dispatchers.IO) {
        server.delete(entity)
    }

    suspend fun deleteServerById(id: Long) = withContext(Dispatchers.IO) {
        server.deleteById(id)
    }
}