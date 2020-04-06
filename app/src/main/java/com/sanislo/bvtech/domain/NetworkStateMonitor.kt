package com.sanislo.bvtech.domain

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log.d
import androidx.lifecycle.LiveData

class NetworkStateMonitor constructor(private val cm: ConnectivityManager) : LiveData<Boolean>() {
    private val networkStateCallback = object : ConnectivityManager.NetworkCallback() {

        //todo BUG: for some reason onLost is called when switching from 4G to wifi, investigate
        override fun onLost(network: Network) {
            super.onLost(network)
            d(TAG, "onLost")
            postValue(false)
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            d(TAG, "onAvailable")
            postValue(true)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            d(TAG, "onUnavailable")
        }
    }

    override fun onActive() {
        super.onActive()
        postValue(isInternetOn())
        cm.registerNetworkCallback(networkRequestBuilder(), networkStateCallback)
    }

    override fun onInactive() {
        super.onInactive()
        cm.unregisterNetworkCallback(networkStateCallback)
    }

    private fun isInternetOn(): Boolean {
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    private fun networkRequestBuilder(): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }

    companion object {
        const val TAG = "NetworkStateMonitor"
    }
}
