package dev.sanmer.whalya.observer

import kotlinx.coroutines.flow.SharedFlow

interface NetworkObserver {
    val state: SharedFlow<State>

    enum class State {
        Available,
        Lost,
        Unavailable;

        val isAvailable inline get() = this == Available
    }
}