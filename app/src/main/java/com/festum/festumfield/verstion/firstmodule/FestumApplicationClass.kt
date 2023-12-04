package com.festum.festumfield.verstion.firstmodule

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates.Companion.get
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp
import io.socket.client.Socket


@HiltAndroidApp
class FestumApplicationClass : MultiDexApplication() {

    companion object {
        lateinit var appInstance: Application
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

        /*initializeSocket()*/

    }

    /*fun getMSocket(): Socket? {
        return mSocket
    }*/


}