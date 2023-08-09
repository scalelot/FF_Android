package com.festum.festumfield.verstion.firstmodule.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        val localBackground = context.let { LocalBroadcastManager.getInstance(it) }
        val localIntent = Intent("NETWORK")
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            localIntent.putExtra("STATUS", "ON")
        } else {
            localIntent.putExtra("STATUS", "OFF")
        }
        localBackground.sendBroadcast(localIntent)
    }

}