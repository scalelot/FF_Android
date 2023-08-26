package com.festum.festumfield.verstion.firstmodule

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.festum.festumfield.MyApplication
import com.festum.festumfield.Utils.Constans.SOCKET_SERVER_URL
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

@HiltAndroidApp
class FestumApplicationClass : MultiDexApplication() {

    companion object {
        lateinit var appInstance: Application
    }

    private var mSocket: Socket? = null

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
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

        initializeSocket()

    }

    fun initializeSocket() {

        val options = IO.Options()
        options.forceNew = true
        options.reconnection = true
        options.reconnectionDelay = 2000
        options.reconnectionDelayMax = 5000
        options.reconnectionAttempts = Int.MAX_VALUE

        try {
            mSocket = IO.socket(SOCKET_SERVER_URL, options)
            mSocket?.on(Socket.EVENT_CONNECT, onConnect)
            mSocket?.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket?.on(Socket.EVENT_DISCONNECT, onDisconnect)

            if (mSocket?.connected() == false) {
                mSocket?.connect()
            }
        } catch (e: Exception) {

            throw java.lang.RuntimeException(e)
        }
    }

    var onConnect: Emitter.Listener = Emitter.Listener {
        Log.e("mSocket", "Socket Connected!")
        try {

            val jsonObj = JSONObject()
            jsonObj.put("channelID", AppPreferencesDelegates.get().channelId)
            mSocket?.emit("init", jsonObj)?.on(
                "userConnected"
            ) { args ->
                val jsonObject = args[0] as JSONObject
                val onlineUser = jsonObject.optString("channelID")
                AppPreferencesDelegates.get().onLineUser = onlineUser
            }

        }catch (e: Exception) {
            Log.e("Error", "error send channalId " + e.message)
        }
    }

    private val onConnectError: Emitter.Listener =
        Emitter.Listener { args -> Log.e("mSocket", args.contentToString()) }

    private val onDisconnect: Emitter.Listener =
        Emitter.Listener { Log.e("mSocket", "onDisconnect") }

    fun getMSocket(): Socket? {
        return mSocket
    }




}