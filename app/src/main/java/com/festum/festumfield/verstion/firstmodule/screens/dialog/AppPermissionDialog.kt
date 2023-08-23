package com.festum.festumfield.verstion.firstmodule.screens.dialog


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.festum.festumfield.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class AppPermissionDialog {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var permissionDialogView: View

        @SuppressLint("InflateParams")
        fun showPermission(
            context: Context,
            message: String,
            title: String
        ) {

            try {

                permissionDialogView = LayoutInflater.from(context).inflate(R.layout.permission_dialog, null, false)
                val allowed = permissionDialogView.findViewById<AppCompatButton>(R.id.dialog_allow)
                val cancel = permissionDialogView.findViewById<AppCompatButton>(R.id.dialogCancel)
                val text = permissionDialogView.findViewById<TextView>(R.id.dis_txt)
                val permissionTitle = permissionDialogView.findViewById<TextView>(R.id.cerate_txt)

                permissionTitle.text = title
                text.text = message
                val dialog = MaterialAlertDialogBuilder(context, R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                    .setCancelable(false)
                    .setView(permissionDialogView)
                    .show()

                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                allowed.setOnClickListener {
                    val i = Intent()
                    i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    i.addCategory(Intent.CATEGORY_DEFAULT)
                    i.data = Uri.parse("package:" + context.packageName)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    context.startActivity(i)
                    dialog.dismiss()
                }
                cancel.setOnClickListener { dialog.dismiss() }

            } catch (e: Exception) {
                Log.e("NoServerDialog", e.message.toString())
            }

        }
    }
}

