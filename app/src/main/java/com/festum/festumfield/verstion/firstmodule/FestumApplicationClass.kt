package com.festum.festumfield.verstion.firstmodule

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.festum.festumfield.Utils.Constans.SOCKET_SERVER_URL
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
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

        SocketManager.initializeSocket()

        /*initializeSocket()*/

    }

    /*fun getMSocket(): Socket? {
        return mSocket
    }*/


}