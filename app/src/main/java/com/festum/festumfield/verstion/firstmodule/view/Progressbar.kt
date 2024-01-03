package com.festum.festumfield.verstion.firstmodule.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.festum.festumfield.R

open class Progressbar constructor(context: Context) : Dialog(context) {
    var dialog: Dialog? = null

    init {
        dialog = Dialog(context)
        val dialogue =
            LayoutInflater.from(context).inflate(R.layout.layout_progressbar, null, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(dialogue)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun showPopup() {
        dialog?.show()
    }

    override fun onBackPressed() {
        dismissPopup()
    }

    fun dismissPopup() {
        dialog?.dismiss()
    }

}
