package com.festum.festumfield.verstion.firstmodule.screens.main.splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.festum.festumfield.databinding.ActivitySplashBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.ApplicationPermissionActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.HomeActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.authorization.LoginActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates.Companion.get
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ProfileViewModel>() {

    private lateinit var binding: ActivitySplashBinding

    var callId : String? = null
    var messageId : String? = null
    var fromId : String? = null
    var toId : String? = null
    var toUserName : String? = null
    var banner : String? = null

    var storge_permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var storge_permissions_33 = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )

    private var listPermissionsNeeded = arrayListOf<String>()

    var perStr = ""

    override fun getContentView(): View {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        val intent = intent
        val extras = intent.extras

        banner = if (extras != null) extras.getString("banner", "") else ""
        callId = if (extras != null) extras.getString("callid", "") else ""
        messageId = if (extras != null) extras.getString("messageid", "") else ""
        fromId = if (extras != null) extras.getString("fromId", "") else ""
        toId = if (extras != null) extras.getString("toId", "") else ""
        toUserName = if (extras != null) extras.getString("toUserName", "") else ""

//        }

        Handler(mainLooper).postDelayed({
            if (get().token.isEmpty()) {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            } else {
                checkPermissions()
            }
        }, 1000)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                } else {
                    for (per in permissions) {
                        perStr += "\n" + per
                    }
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        var result = 0
        listPermissionsNeeded.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            for (p in storge_permissions_33) {
                result = ContextCompat.checkSelfPermission(applicationContext, p)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p)
                }
            }
        } else {
            for (p in storge_permissions) {
                result = ContextCompat.checkSelfPermission(applicationContext, p)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p)
                }
            }
        }
        Log.e("checkPermissions:", listPermissionsNeeded.toString())
        if (listPermissionsNeeded.isNotEmpty()) {
            startActivity(Intent(this@SplashActivity, ApplicationPermissionActivity::class.java))
            return false
        } else {
            val intent = Intent(this@SplashActivity, HomeActivity::class.java)
            intent.putExtra("callId", callId)
            intent.putExtra("banner", banner)
            intent.putExtra("messageId", messageId)
            intent.putExtra("fromId", fromId)
            intent.putExtra("toId", toId)
            intent.putExtra("toId", toId)
            startActivity(intent)

            /*if (!callId.isEmpty()){

                Intent intent = new Intent();
                intent.putExtra("callId",callId);
                intent.putExtra("fromId",fromId);
                intent.putExtra("toId",toId);
                intent.putExtra("fromId",fromId);
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));

            }else {

            }*/
        }
        return true
    }

    override fun setupObservers() {

    }

}