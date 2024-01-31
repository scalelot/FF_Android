package com.festum.festumfield.verstion.firstmodule.screens.main

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.text.HtmlCompat
import com.festum.festumfield.Activity.MapsLocationActivity
import com.festum.festumfield.R
import com.festum.festumfield.databinding.ActivityApplicationPermissionBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.sources.local.model.Permissions
import com.festum.festumfield.verstion.firstmodule.utils.LocationUtils
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import java.security.Permission


@AndroidEntryPoint
class ApplicationPermissionActivity : BaseActivity<ProfileViewModel>() {

    private lateinit var binding: ActivityApplicationPermissionBinding

    /*var storge_permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
    )*/

    lateinit var permission: Array<String>

    override fun getContentView(): View {
        binding = ActivityApplicationPermissionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        binding.txtResendCode.text = Html.fromHtml(getString(R.string.location_disclosure_dialog_message),Html.FROM_HTML_MODE_LEGACY)

        binding.btnVerify.setOnClickListener {

            dexterPermission()

        }

    }

    override fun setupObservers() {

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startActivity(Intent(this@ApplicationPermissionActivity, HomeActivity::class.java))

            } else  {
                onPermission()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onPermission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport) {

                        if (permission.areAllPermissionsGranted()) {

                            startActivity(Intent(this@ApplicationPermissionActivity, HomeActivity::class.java))

                        } else {

                            Toast.makeText(this@ApplicationPermissionActivity, "Permission Grated", Toast.LENGTH_SHORT).show()
                            showLocationDisclosure()

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                        p1: PermissionToken
                    ) {
                        p1.continuePermissionRequest()
                    }

                }).withErrorListener { error ->
                    // we are displaying a toast message for error message.
                    Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT)
                        .show()
                } // below line is use to run the permissions on same thread and to check the permissions
                .onSameThread().check()
        } else {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport) {

                        if (permission.areAllPermissionsGranted()) {

                            startActivity(Intent(this@ApplicationPermissionActivity, HomeActivity::class.java))

                        } else {

                            showLocationDisclosure()

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                        p1: PermissionToken
                    ) {
                        p1.continuePermissionRequest()
                    }

                }).withErrorListener { error ->
                    // we are displaying a toast message for error message.
                    Log.e("TAG", "dexterPermission:------ "  + error.name )
                } // below line is use to run the permissions on same thread and to check the permissions
                .onSameThread().check()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun showLocationDisclosure() {
        MaterialAlertDialogBuilder(this, R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
            .setMessage(getString(R.string.permission_check))
            .setPositiveButton(R.string.location_disclosure_dialog_positive_button) { dialog, which ->
                val i = Intent()
                i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                i.addCategory(Intent.CATEGORY_DEFAULT)
                i.data = Uri.parse("package:$packageName")
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                startActivity(i)
                dialog.dismiss()
            }
            .show()
    }

    private fun dexterPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            Dexter.withContext(this).withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {

                        if (multiplePermissionsReport.areAllPermissionsGranted()) {

                            startActivity(Intent(this@ApplicationPermissionActivity, HomeActivity::class.java))

                        }  else {

                            onLocationCheck(multiplePermissionsReport)

                        }

                        /*if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                            onPermissionDialog()
                            *//*startActivity(Intent(this@ApplicationPermissionActivity, HomeActivity::class.java))*//*
                        }*/
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                        p1: PermissionToken
                    ) {
                        p1.continuePermissionRequest()
                    }

                }).withErrorListener { error ->
                    // we are displaying a toast message for error message.
                    Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT)
                        .show()
                } // below line is use to run the permissions on same thread and to check the permissions
                .onSameThread().check()
        } else {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {

                        if (multiplePermissionsReport.areAllPermissionsGranted()) {

                            startActivity(Intent(this@ApplicationPermissionActivity, HomeActivity::class.java))

                        } else {

                            onLocationCheck(multiplePermissionsReport)

                        }

                        /*if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {

                            onPermissionDialog()
                            *//*startActivity(Intent(this@ApplicationPermissionActivity, HomeActivity::class.java))*//*
                        }*/
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                        p1: PermissionToken
                    ) {
                        p1.continuePermissionRequest()
                    }

                }).withErrorListener { error ->
                    // we are displaying a toast message for error message.
                    Log.e("TAG", "dexterPermission:------ "  + error.name )
                } // below line is use to run the permissions on same thread and to check the permissions
                .onSameThread().check()
        }

    }

    private fun onPermissionDialog() {

        MaterialAlertDialogBuilder(this, R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
            .setMessage(getString(R.string.application_permission_message))
            .setPositiveButton(getString(R.string.go_to_settings)) { dialog: DialogInterface, which: Int ->

                dialog.dismiss()

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }.show()


    }

    private fun onLocationCheck(multiplePermissionsReport : MultiplePermissionsReport){
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (_: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) {
        }

        if (!gps_enabled && !network_enabled) {

            MaterialAlertDialogBuilder(
                this,
                R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog
            )
                .setTitle(getString(R.string.gps_enable))
                .setMessage(getString(R.string.turn_on_your_location))
                .setPositiveButton("OK") { dialogInterface, i ->
                    dialogInterface.dismiss()

                }
                .show()
        } else if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied){

            onPermissionDialog()

        } else {
            startActivity(
                Intent(
                    this@ApplicationPermissionActivity,
                    HomeActivity::class.java
                ).putExtra("isProfileLocation", true)
            )
        }

    }

}