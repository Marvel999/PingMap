package com.marvel999.pingmap.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Observes network connectivity. Used to show "No internet" and disable actions when offline.
 */
class NetworkMonitor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isConnected: Boolean
        get() = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

    fun connectivityFlow(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            override fun onLost(network: Network) {
                trySend(connectivityManager.activeNetwork == null)
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)
        trySend(isConnected)
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }
}
