package com.festum.festumfield.verstion.firstmodule.sources.remote.apis

import android.util.Log
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.di.ApiModule
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

object SocketManager {

    var mSocket: Socket? = null

    fun initializeSocket() {

        val options = IO.Options()
        options.forceNew = true
        options.reconnection = true
        options.reconnectionDelay = 2000
        options.reconnectionDelayMax = 5000
        options.reconnectionAttempts = Int.MAX_VALUE

        try {
            mSocket = IO.socket(ApiModule.BASE_URL_SOCKET, options)
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

    private var onConnect: Emitter.Listener = Emitter.Listener { args ->

        AppPreferencesDelegates.get().isSocketId = mSocket?.id().toString()

        try {

            val jsonObj = JSONObject()
            jsonObj.put("channelID", AppPreferencesDelegates.get().channelId)
            mSocket?.emit("init", jsonObj)?.on(
                "userConnected"
            ) { args ->
                val jsonObject = args[0] as JSONObject
                AppPreferencesDelegates.get().onLineUser = jsonObject.optJSONObject("onlineUsers")?.toString() ?: ""

            }

        }catch (e: Exception) {
            Log.e("Error", "error send channalId " + e.message)
        }
    }

    private val onConnectError: Emitter.Listener =
        Emitter.Listener { args -> Log.e("mSocket", args.contentToString()) }

    private val onDisconnect: Emitter.Listener =
        Emitter.Listener { Log.e("mSocket", "onDisconnect") }

    fun connected() : Boolean? {
        return mSocket?.connected()
    }

    fun connectSocket() {
        mSocket?.connect()
    }
    fun disconnectSocket() {
        mSocket?.disconnect()
    }

}