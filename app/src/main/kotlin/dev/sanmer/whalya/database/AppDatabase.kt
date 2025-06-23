package dev.sanmer.whalya.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanmer.whalya.database.dao.ServerDao
import dev.sanmer.whalya.database.entity.ServerEntity
import dev.sanmer.whalya.ktx.deviceProtectedContext
import javax.inject.Singleton

@Database(version = 1, entities = [ServerEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun sever(): ServerDao

    companion object Builder {
        operator fun invoke(context: Context) =
            Room.databaseBuilder<AppDatabase>(
                context = context,
                name = "server"
            ).build()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object Impl {
        @Provides
        @Singleton
        fun AppDatabase(
            @ApplicationContext context: Context
        ) = Builder(context.deviceProtectedContext)

        @Provides
        @Singleton
        fun ServerDao(db: AppDatabase) = db.sever()
    }
}