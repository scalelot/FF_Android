package com.festum.festumfield.verstion.firstmodule.helper

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import com.festum.festumfield.databinding.UpcomingCallBinding
import com.festum.festumfield.verstion.firstmodule.screens.main.chat.IncomingCallActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.profile.ProfilePreviewActivity
import org.json.JSONObject

class MyBroadcastReceiver: BroadcastReceiver() {
    companion object{
       var isOpen = false
    }
    override fun onReceive(context: Context, intent: Intent?) {

        val jsonString = intent?.getStringExtra("jsonData").toString()

        if (context.isContextValid()) {

            if (!isOpen){
                val newActivityIntent = Intent(context, IncomingCallActivity::class.java)
                newActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                newActivityIntent.putExtra("jsonData",jsonString)
                context.startActivity(newActivityIntent)
                isOpen = true
            }

        }

    }

    private fun Context.isContextValid(): Boolean {
        if (this is ContextWrapper) {
            val baseContext = this.baseContext
            return !(baseContext is Activity && (baseContext.isFinishing || baseContext.isDestroyed))
        }
        return false
    }

}