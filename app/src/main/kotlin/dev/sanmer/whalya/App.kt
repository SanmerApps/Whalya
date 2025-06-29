package dev.sanmer.whalya

import android.app.Application
import dev.sanmer.whalya.di.database
import dev.sanmer.whalya.di.repositories
import dev.sanmer.whalya.di.viewModels
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.lsposed.hiddenapibypass.HiddenApiBypass

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        HiddenApiBypass.setHiddenApiExemptions("")
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(database, repositories, viewModels)
        }
    }
}
