package dev.sanmer.whalya.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.sanmer.whalya.database.dao.ServerDao
import dev.sanmer.whalya.database.entity.ServerEntity

@Database(version = 1, entities = [ServerEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun sever(): ServerDao

    companion object Default {
        fun build(context: Context) =
            Room.databaseBuilder<AppDatabase>(
                context = context,
                name = "server"
            ).build()
    }
}