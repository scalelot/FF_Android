package com.festum.festumfield.verstion.firstmodule.screens.main.authorization

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.festum.festumfield.Activity.LoginVerifyActivity
import com.festum.festumfield.Model.SendOtpModel
import com.festum.festumfield.R
import com.festum.festumfield.Utils.FileUtils
import com.festum.festumfield.databinding.ActivityLoginBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.dialog.CodeDialog
import com.festum.festumfield.verstion.firstmodule.sources.local.model.PhoneCodeModel
import com.festum.festumfield.verstion.firstmodule.sources.local.model.SendOtpBody
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates.Companion.get
import com.festum.festumfield.verstion.firstmodule.utils.CountryCityUtils.Companion.firstTwo
import com.festum.festumfield.verstion.firstmodule.utils.CountryCityUtils.Companion.getFlagId
import com.festum.festumfield.verstion.firstmodule.utils.FileUtil.Companion.loadJSONFromAsset
import com.festum.festumfield.verstion.firstmodule.viemodels.AuthViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale
import java.util.Objects
@AndroidEntryPoint
class LoginActivity : BaseActivity<AuthViewModel>(), CodeDialog.CountyPickerItems {

    private lateinit var binding: ActivityLoginBinding

    private var countycode: String? = null

    private var selectedPhoneModel: PhoneCodeModel? = null
    override fun getContentView(): View {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        /** getFirebaseToken **/
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
                s -> get().fcmToken = s.toString()
        }.addOnFailureListener {
                e -> Log.e("TAG", e.toString())
        }


        try {
            getCountryCode()
        } catch (e: JSONException) {
            Log.e("TAG", "onCreate:--+-- " + e.localizedMessage)
            throw RuntimeException(e)
        }

        binding.btnContinue.setOnClickListener { it ->

            try {
                FileUtils.hideKeyboard(this@LoginActivity)
                val newToken = get().fcmToken
                val phoneNumber: String = binding.edtPhone.text.toString().trim { it <= ' ' }

                Log.e("TAG", "onClick:--- $countycode")

                if (phoneNumber.isEmpty()) {
                    binding.edtPhone.error = "Enter Mobile Number"
                } else {
                    if (isValidPhoneNumber(phoneNumber)) {
                        val status: Boolean = validateUsingPhoneNumber(countycode.toString(), phoneNumber)
                        if (status) {

                            val sendOtpBody = SendOtpBody(
                                countryCode = countycode,
                                contactNo = binding.edtPhone.text.toString(),
                                fcmtoken = newToken
                            )
                            viewModel.sendOtp(sendOtpBody)

                            val intent = Intent(this@LoginActivity, LoginVerifyActivity::class.java)
                               /* .putExtra("otpToken", sendOtpData.token)*/
                                .putExtra("countryCode", countycode)
                                .putExtra("phoneNumber", binding.edtPhone.text.toString())
                            startActivity(intent)

                            FileUtils.DisplayLoading(this@LoginActivity)

                            val b = it as Button
                            b.isEnabled = false
                        } else {
                            binding.edtPhone.error = "Invalid Phone Number"
                        }
                    } else {
                        binding.edtPhone.error = "Invalid Phone Number"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun setupObservers() {

        viewModel.sendOtpData.observe(this){ sendOtpData ->

            if (sendOtpData != null){

                /*startActivity(
                    Intent(this@LoginActivity, LoginVerifyActivity::class.java)
                        .putExtra("otpToken", sendOtpData.token)
                        .putExtra("countryCode", countycode)
                        .putExtra("phoneNumber", binding.edtPhone.text.toString())
                )*/
                /*runOnUiThread{

                    val intent = Intent(this@LoginActivity, LoginVerifyActivity::class.java)
                        .putExtra("otpToken", sendOtpData.token)
                        .putExtra("countryCode", countycode)
                        .putExtra("phoneNumber", binding.edtPhone.text.toString())
                    startActivity(intent)

                }*/

            }

        }

    }
    private fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        return if (!TextUtils.isEmpty(phoneNumber)) {
            Patterns.PHONE.matcher(phoneNumber).matches()
        } else false
    }

    private fun validateUsingPhoneNumber(countryCode: String, phNumber: String): Boolean {
        val phoneNumberUtil = PhoneNumberUtil.createInstance(this)
        val isoCode = phoneNumberUtil.getRegionCodeForCountryCode(countryCode.toInt())
        var phoneNumber: PhoneNumber? = null
        try {
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode)
        } catch (e: NumberParseException) {
            System.err.println(e)
        }
        val isValid = phoneNumberUtil.isValidNumber(phoneNumber)
        return if (isValid) {
            true
        } else {
            binding.edtPhone.error = "Invalid Phone Number"
            false
        }
    }

    override fun onResume() {
        super.onResume()
        binding.edtPhone.setText("")
        val button1 = findViewById<View>(R.id.btn_continue) as AppCompatButton
        button1.isEnabled = true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    @SuppressLint("SetTextI18n")
    @Throws(JSONException::class)
    fun getCountryCode() {
        val phoneModelList = ArrayList<PhoneCodeModel>()
        val obj = JSONObject(Objects.requireNonNull(loadJSONFromAsset(this)))
        val keys = obj.keys()
        while (keys.hasNext()) {
            val keyStr = keys.next()
            try {
                val keyValue = obj.getString(keyStr)
                val code = PhoneCodeModel(keyStr, keyValue)
                phoneModelList.add(code)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        if (selectedPhoneModel == null) {
            selectedPhoneModel = phoneModelList[86]
        }
        binding.countryCode.text = "+ " + selectedPhoneModel?.value
        binding.countryCode.text = binding.countryCode.text.toString()
        countycode = selectedPhoneModel?.value
        binding.codeFlag.text = getFlagId(
            firstTwo(
                Objects.requireNonNull<String>(selectedPhoneModel?.key)
                    .lowercase(Locale.getDefault())
            )
        )
        val phoneCodeDialog = CodeDialog(this, phoneModelList, this)
        binding.linear.setOnClickListener(View.OnClickListener { view: View? ->
            if (!phoneCodeDialog.isAdded) {
                phoneCodeDialog.show(supportFragmentManager, null)
            }
        })
    }



    @SuppressLint("SetTextI18n")
    override fun pickCountry(countries: PhoneCodeModel) {

        selectedPhoneModel = countries
        binding.countryCode.text = "+ " + countries.value
        countycode = countries.value
        binding.codeFlag.text = getFlagId(
            firstTwo(
                Objects.requireNonNull<String?>(countries.key).lowercase(Locale.getDefault())
            )
        )
    }

}