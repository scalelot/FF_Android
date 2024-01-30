package com.festum.festumfield.verstion.firstmodule

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.festum.festumfield.verstion.firstmodule.helper.MyBroadcastReceiver
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates.Companion.get
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp
import io.socket.client.Socket
import org.json.JSONObject


@HiltAndroidApp
class FestumApplicationClass : MultiDexApplication() {

    companion object {
        lateinit var appInstance: Application
        var isAppInForeground = false
//        init {
//            System.loadLibrary("jingle_peerconnection")
//        }

    }

    private var mSocket: Socket? = null

    override fun attachBaseContext(base: Context?) {

        MultiDex.install(base)
        super.attachBaseContext(base)

    }

    override fun onCreate() {
        super.onCreate()
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        appInstance = this

        SocketManager.initializeSocket()

        get().isFirstStart = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && get().isFirstStart) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else {
            if (get().isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        registerBroadcastReceiver()


        SocketManager.mSocket?.on("incomingCall") { args ->

            val data = args[0] as JSONObject

            MyBroadcastReceiver.isOpen = false

            val jsonString = data.toString()

            val intent = Intent("com.festumfield.BROADCAST_ACTION")
            intent.putExtra("jsonData",jsonString)
            sendBroadcast(intent)


        }

        /*initializeSocket()*/

    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerBroadcastReceiver() {

        /*registerReceiver(MyBroadcastReceiver(), filter)*/

        val filter = IntentFilter("com.festumfield.BROADCAST_ACTION")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(MyBroadcastReceiver(), filter, RECEIVER_EXPORTED)
        }else {
            registerReceiver(MyBroadcastReceiver(), filter)
        }
    }



    /*fun getMSocket(): Socket? {
        return mSocket
    }*/


}