package dev.sanmer.whalya.di

import android.content.Context
import dev.sanmer.whalya.database.AppDatabase
import dev.sanmer.whalya.ktx.deviceProtectedContext
import org.koin.dsl.module

val Database = module {
    single {
        AppDatabase.build(get<Context>().deviceProtectedContext)
    }

    single {
        get<AppDatabase>().sever()
    }
}