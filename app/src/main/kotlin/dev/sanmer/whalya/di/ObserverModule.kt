package dev.sanmer.whalya.di

import androidx.lifecycle.ProcessLifecycleOwner
import dev.sanmer.whalya.observer.ForegroundObserver
import dev.sanmer.whalya.observer.ForegroundObserverImpl
import org.koin.dsl.module

val Observers = module {
    single {
        ProcessLifecycleOwner.get()
    }

    single<ForegroundObserver> {
        ForegroundObserverImpl(get())
    }
}