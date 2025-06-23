package dev.sanmer.whalya

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.lsposed.hiddenapibypass.HiddenApiBypass
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    init {
        Timber.plant(Timber.DebugTree())
    }

    override fun onCreate() {
        super.onCreate()
        HiddenApiBypass.setHiddenApiExemptions("")
    }
}
