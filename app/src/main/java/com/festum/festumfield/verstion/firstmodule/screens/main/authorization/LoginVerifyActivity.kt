package com.festum.festumfield.verstion.firstmodule.screens.main.authorization

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.CountDownTimer
import android.view.View
import androidx.annotation.RequiresApi
import com.festum.festumfield.R
import com.festum.festumfield.Utils.FileUtils
import com.festum.festumfield.databinding.ActivityLoginVerifyBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.viemodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import `in`.aabhasjindal.otptextview.OTPListener

@AndroidEntryPoint
class LoginVerifyActivity : BaseActivity<AuthViewModel>() {

    private lateinit var binding: ActivityLoginVerifyBinding

    private var otpToken : String ?= null
    private var otpData : String = ""

    var storagePermissions = arrayOf(
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
    var storagePermissions33 = arrayOf(
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

    var listPermissionsNeeded = arrayListOf<String>()
    var perStr = ""

    override fun getContentView(): View {
        binding = ActivityLoginVerifyBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun setupUi() {

        if (intent != null){

            otpToken = intent.getStringExtra("otpToken")
            val countryCode = intent.getStringExtra("countryCode")
            val mobileNumber = intent.getStringExtra("phoneNumber")

            binding.txtPhoneNumber.text = " +$countryCode $mobileNumber"

        }

        showTimer()

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.otpTextView.otpListener = object : OTPListener {
            override fun onInteractionListener() {}
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onOTPComplete(otp: String) {
                otpData = otp
                if (otp.isEmpty()) {
                    binding.btnVerify.background = resources.getDrawable(R.drawable.verify_btn_bg)
                } else {
                    binding.btnVerify.background = resources.getDrawable(R.drawable.login_btn_bg)
                }
            }
        }

        binding.btnVerify.setOnClickListener {
            FileUtils.hideKeyboard(this@LoginVerifyActivity)
            if (otpData.length > 3) {
                FileUtils.DisplayLoading(this@LoginVerifyActivity)
                /*VerifyOtp(Snackbar)*/
            } else {
                val snackbar = Snackbar.make(binding.llLinear, "Please Enter Otp", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        }

    }

    override fun setupObservers() {

    }

    fun showTimer() {
        binding.txtResendCode.isEnabled = false
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                binding.tvTimer.text = String.format("%02d:%02d", seconds / 60, seconds % 60)
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                binding.txtResendCode.isEnabled = true
            }
        }.start()
    }

}