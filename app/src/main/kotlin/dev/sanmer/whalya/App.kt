package dev.sanmer.whalya

import android.app.Application
import dev.sanmer.whalya.di.Database
import dev.sanmer.whalya.di.Observers
import dev.sanmer.whalya.di.Repositories
import dev.sanmer.whalya.di.ViewModels
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
            modules(Database, Repositories, Observers, ViewModels)
        }
    }
}
