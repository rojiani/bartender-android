package com.nrojiani.bartender.utils.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

/**
 * Provides network status information as a [Flow].
 */
class NetworkStatusMonitor @Inject constructor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkEventsFlow: Flow<NetworkStatus> = callbackFlow {
        Timber.d("[NetworkStatusMonitor] callbackFlow started")

        trySend(NetworkStatus.UNDETERMINED)

        val initial = when {
            connectivityManager.activeNetwork != null -> NetworkStatus.CONNECTED
            else -> NetworkStatus.NOT_CONNECTED
        }
        trySend(initial)

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Timber.i("[NetworkCallback:onAvailable] network = $network")
                trySend(NetworkStatus.CONNECTED)
            }

            override fun onLost(network: Network) {
                Timber.i("[NetworkCallback:onLost] network =  The last default network was $network")
                trySend(NetworkStatus.NOT_CONNECTED)
            }
        }

        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build(),
            networkCallback
        )

        // Suspends until channel closed
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}
