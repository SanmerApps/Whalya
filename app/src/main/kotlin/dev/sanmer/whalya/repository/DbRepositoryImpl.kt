package dev.sanmer.whalya.repository

import dev.sanmer.whalya.database.dao.ServerDao
import dev.sanmer.whalya.database.entity.ServerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext

class DbRepositoryImpl(
    private val serverDao: ServerDao
) : DbRepository {
    override fun getServersAsFlow() = serverDao.getAllAsFlow()

    override fun getServerByIdAsFlow(id: Long) = serverDao.getByIdAsFlow(id).filterNotNull()

    override suspend fun getServers() = withContext(Dispatchers.IO) {
        serverDao.getAll()
    }

    override suspend fun insertServer(server: ServerEntity) = withContext(Dispatchers.IO) {
        serverDao.insert(server)
    }

    override suspend fun updateServer(server: ServerEntity) = withContext(Dispatchers.IO) {
        serverDao.update(server)
    }

    override suspend fun deleteServer(server: ServerEntity) = withContext(Dispatchers.IO) {
        serverDao.delete(server)
    }

    override suspend fun deleteServerById(id: Long) = withContext(Dispatchers.IO) {
        serverDao.deleteById(id)
    }
}