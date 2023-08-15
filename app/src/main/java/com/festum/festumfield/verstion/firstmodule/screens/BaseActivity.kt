package com.festum.festumfield.verstion.firstmodule.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.festum.festumfield.verstion.firstmodule.navigation.UiEvent
import com.festum.festumfield.verstion.firstmodule.utils.DeviceUtils
import java.lang.reflect.ParameterizedType


abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {

    lateinit var viewModel: VM

    companion object {
        var profileLogoListener: OnProfileLogoChangeListener? = null
        var isFragmentVisible = false
        var sManager: FragmentManager? = null
//        var fragment = InternetBottomSheetDialog()
    }

    interface OnProfileLogoChangeListener {
        fun onCropLogo(uri: Uri)
        fun onChangeLogo(uri: Uri)
    }

    private var mNetworkReceiver: BroadcastReceiver? = null
    private var networkReceiver: NetworkReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())

        viewModel = ViewModelProvider(this)[getViewModelClass()]

        viewModel.uiEventStream.observe(this) { it -> processUiEvent(it) }

        setupUi()
        setupObservers()
        viewModel.onActivityCreated()

        sManager = supportFragmentManager

        //Internet Connection
        Handler(Looper.getMainLooper()).postDelayed({

            mNetworkReceiver = NetworkChangeReceiver()
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
            networkReceiver = NetworkReceiver()
            networkReceiver.let {
                if (it != null) {
                    LocalBroadcastManager.getInstance(baseContext).registerReceiver(
                        it,
                        IntentFilter("NETWORK")
                    )
                }
            }

        }, 0)

    }


    abstract fun getContentView(): View
    abstract fun setupUi()
    abstract fun setupObservers()


    private fun getViewModelClass(): Class<VM> {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        return type as Class<VM>
    }

    fun processUiEvent(uiEvent: UiEvent) {

        when (uiEvent) {

            is UiEvent.HideKeyboard -> {
                DeviceUtils.hideKeyboard(this)
            }
            is UiEvent.ShowToast -> {
                showToast(getString(uiEvent.messageResId))
            }
            is UiEvent.ShowToastMsg -> {
                showToast(uiEvent.message)
            }
        }
    }

    private fun showToast(message: String) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mNetworkReceiver != null)
            unregisterReceiver(mNetworkReceiver)


    }

    private class NetworkReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val status = intent?.getStringExtra("STATUS")
            if (status.equals("OFF")) {
                if (!isFragmentVisible) {
//                    try {
//                        sManager?.let { fragment.show(it, "no_internet") }
//                        isFragmentVisible = true
//                    } catch (e: Exception) {
//                    }
                }
            } else {
                if (isFragmentVisible) {
//                    fragment.dismiss()
                    isFragmentVisible = false
                }
            }
        }

    }
}