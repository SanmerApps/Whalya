package dev.sanmer.whalya.compat

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService

object ConnectivityManagerCompat {
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService<ConnectivityManager>() ?: return false
        return cm.isConnected(cm.activeNetwork)
    }

    fun ConnectivityManager.isConnected(network: Network?): Boolean {
        val networkCapabilities = getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}