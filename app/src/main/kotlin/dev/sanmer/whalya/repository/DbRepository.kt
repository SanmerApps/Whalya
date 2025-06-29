package dev.sanmer.whalya.repository

import dev.sanmer.whalya.database.entity.ServerEntity
import kotlinx.coroutines.flow.Flow

interface DbRepository {
    fun getServersAsFlow(): Flow<List<ServerEntity>>
    fun getServerByIdAsFlow(id: Long): Flow<ServerEntity>
    suspend fun getServers(): List<ServerEntity>
    suspend fun insertServer(server: ServerEntity)
    suspend fun updateServer(server: ServerEntity)
    suspend fun deleteServer(server: ServerEntity)
    suspend fun deleteServerById(id: Long)
}