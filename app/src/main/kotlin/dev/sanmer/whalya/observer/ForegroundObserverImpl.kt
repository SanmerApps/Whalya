package dev.sanmer.whalya.observer

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.shareIn

class ForegroundObserverImpl(
    lifecycleOwner: LifecycleOwner
) : ForegroundObserver {
    override val isForeground = callbackFlow {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                trySend(true)
            }

            override fun onStop(owner: LifecycleOwner) {
                trySend(false)
            }
        }
        lifecycle.addObserver(observer)
        trySend(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
        awaitClose {
            lifecycle.removeObserver(observer)
        }
    }.distinctUntilChanged()
        .shareIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
            started = SharingStarted.Eagerly,
            replay = 1
        )

}