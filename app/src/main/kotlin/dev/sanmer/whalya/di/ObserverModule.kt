package dev.sanmer.whalya.di

import androidx.lifecycle.ProcessLifecycleOwner
import dev.sanmer.whalya.observer.ForegroundObserver
import dev.sanmer.whalya.observer.ForegroundObserverImpl
import dev.sanmer.whalya.observer.NetworkObserver
import dev.sanmer.whalya.observer.NetworkObserverImpl
import org.koin.dsl.module

val Observers = module {
    single {
        ProcessLifecycleOwner.get()
    }

    single<ForegroundObserver> {
        ForegroundObserverImpl(get())
    }

    single<NetworkObserver> {
        NetworkObserverImpl(get())
    }
}