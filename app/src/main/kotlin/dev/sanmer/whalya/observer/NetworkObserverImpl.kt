package dev.sanmer.whalya.observer

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import dev.sanmer.whalya.compat.ConnectivityManagerCompat.isConnected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.shareIn

class NetworkObserverImpl(
    context: Context
) : NetworkObserver {
    override val state = callbackFlow {
        val cm = context.getSystemService<ConnectivityManager>()
        if (cm == null) {
            trySend(NetworkObserver.State.Unavailable)
            return@callbackFlow
        }
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(
                    if (cm.isConnected(network))
                        NetworkObserver.State.Available
                    else
                        NetworkObserver.State.Unavailable
                )
            }

            override fun onLost(network: Network) {
                trySend(NetworkObserver.State.Lost)
            }

            override fun onUnavailable() {
                trySend(NetworkObserver.State.Unavailable)
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(request, callback)
        trySend(
            if (cm.isConnected(cm.activeNetwork))
                NetworkObserver.State.Available
            else
                NetworkObserver.State.Unavailable
        )
        awaitClose {
            cm.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
        .shareIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            started = SharingStarted.Eagerly,
            replay = 1
        )

}