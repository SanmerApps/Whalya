package dev.sanmer.whalya.observer

import kotlinx.coroutines.flow.SharedFlow

interface ForegroundObserver {
    val isForeground: SharedFlow<Boolean>
}