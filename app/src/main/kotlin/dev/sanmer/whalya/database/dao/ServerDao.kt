package dev.sanmer.whalya.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.sanmer.whalya.database.entity.ServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Query("SELECT * FROM server")
    fun getAllAsFlow(): Flow<List<ServerEntity>>

    @Query("SELECT * FROM server WHERE id = :id")
    fun getByIdAsFlow(id: Long): Flow<ServerEntity?>

    @Query("SELECT * FROM server")
    suspend fun getAll(): List<ServerEntity>

    @Insert
    suspend fun insert(server: ServerEntity)

    @Update
    suspend fun update(server: ServerEntity)

    @Delete
    suspend fun delete(server: ServerEntity)

    @Query("DELETE FROM server WHERE id = :id")
    suspend fun deleteById(id: Long)
}