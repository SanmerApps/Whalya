package dev.sanmer.docker.compat

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService

object ConnectivityManagerCompat {
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService<ConnectivityManager>() ?: return false
        val networkCapabilities = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}