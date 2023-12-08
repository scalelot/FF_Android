package com.festum.festumfield.verstion.firstmodule.screens.main.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.festum.festumfield.Activity.MapsLocationActivity
import com.festum.festumfield.Activity.ProductActivity
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Const
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ActivityCreateBusinessBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CreateBusinessProfileModel
import com.festum.festumfield.verstion.firstmodule.utils.FileUtil
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageActivity
import com.theartofdev.edmodo.cropper.CropImageOptions
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class CreateBusinessProfileActivity : BaseActivity<ProfileViewModel>(),
    OnMapReadyCallback {


    private lateinit var binding: ActivityCreateBusinessBinding

    private var map: GoogleMap? = null

    override fun getContentView(): View {
        binding = ActivityCreateBusinessBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        val editProfile = intent.getStringExtra("EditProfile")

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync(this)
        binding.mapview.getMapAsync(this)

        if (editProfile?.isNotEmpty() == true) {
            binding.tvTitle.text = editProfile
            binding.rlUpload.visibility = View.GONE
            binding.rlAddBrochur.visibility = View.VISIBLE
        } else {
            binding.rlUpload.visibility = View.VISIBLE
            binding.rlAddBrochur.visibility = View.GONE
        }

        viewModel.getBusinessProfile()

        binding.icBack.setOnClickListener {
            finish()
        }

        binding.edtImg.setOnClickListener {
            openImageOrDocument(R.id.edt_img)
        }

        binding.rlUpload.setOnClickListener {
            openImageOrDocument(R.id.rl_upload)
        }

        binding.btnChange.setOnClickListener {
            openImageOrDocument(R.id.btn_change)
        }

        binding.relativeBusiMap.setOnClickListener {

            if (IntentUtil.readMapPermission(
                    this@CreateBusinessProfileActivity
                ) && IntentUtil.readMapFinePermission(
                    this@CreateBusinessProfileActivity
                )
            ) {
                onLocationCheck()

            } else
                AppPermissionDialog.showPermission(
                    this@CreateBusinessProfileActivity,
                    title = getString(R.string.location_permission_title),
                    message = getString(R.string.location_permission)
                )

        }

        binding.btnNext.setOnClickListener {

            val businessProfile = CreateBusinessProfileModel(
                name = binding.edtBussinessName.text.toString(),
                category = binding.edtCategory.text.toString(),
                subCategory = binding.edtSubcategory.text.toString(),
                description = binding.edtDescription.text.toString(),
                interestedCategory = binding.edtInterestedCategory.text.toString(),
                interestedSubCategory = binding.edtInterestedSubcategory.text.toString(),
                latitude = Const.lattitude,
                longitude = Const.longitude
            )

            if (businessProfile.name.isNullOrEmpty()){
                binding.edtBussinessName.requestFocus()
                binding.edtBussinessName.error = resources.getString(R.string.please_enter_bname)
                Snackbar.make(binding.linear, resources.getString(R.string.please_enter_bname), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (businessProfile.category.isNullOrEmpty()){
                binding.edtCategory.requestFocus()
                binding.edtCategory.error = resources.getString(R.string.please_enter_b_cat)
                Snackbar.make(binding.linear, resources.getString(R.string.please_enter_b_cat), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (businessProfile.subCategory.isNullOrEmpty()){
                binding.edtSubcategory.requestFocus()
                binding.edtSubcategory.error = resources.getString(R.string.please_enter_b_subcat)
                Snackbar.make(binding.linear, resources.getString(R.string.please_enter_b_subcat), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (businessProfile.description.isNullOrEmpty()){
                binding.edtDescription.requestFocus()
                binding.edtDescription.error = resources.getString(R.string.please_enter_b_des)
                Snackbar.make(binding.linear, resources.getString(R.string.please_enter_b_des), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Const.b_lattitude == null){
                Snackbar.make(binding.linear, resources.getString(R.string.please_enter_b_location), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (businessProfile.interestedCategory.isNullOrEmpty()){
                binding.edtInterestedCategory.requestFocus()
                binding.edtInterestedCategory.error = resources.getString(R.string.please_enter_interested_category)
                Snackbar.make(binding.linear, resources.getString(R.string.please_enter_interested_category), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (businessProfile.interestedSubCategory.isNullOrEmpty()){
                binding.edtInterestedSubcategory.requestFocus()
                binding.edtInterestedSubcategory.error = resources.getString(R.string.please_enter_interested_subcategory)
                Snackbar.make(binding.linear, resources.getString(R.string.please_enter_interested_subcategory), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.createBusinessProfile(businessProfile)

        }


    }

    @SuppressLint("SetTextI18n")
    override fun setupObservers() {

        viewModel.profileBusinessData.observe(this) { profileBusinessData ->

            if (profileBusinessData != null) {

                Glide.with(this@CreateBusinessProfileActivity)
                    .load(Constans.Display_Image_URL + profileBusinessData.businessimage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.businessProfileImage)

                binding.edtBussinessName.setText(profileBusinessData.name)
                binding.edtCategory.setText(profileBusinessData.category)
                binding.edtSubcategory.setText(profileBusinessData.subCategory)
                binding.edtDescription.setText(profileBusinessData.description)

                if (profileBusinessData.brochure != null) {

                    val brochure: String = profileBusinessData.brochure
                    val pdfName = brochure.substring(brochure.lastIndexOf('/') + 1)
                    binding.edtBrochure.text = pdfName
                    binding.edtBrochure.setOnClickListener {
                        val uri =
                            Uri.parse(Constans.Display_Image_URL + profileBusinessData.brochure)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }

                } else {

                    binding.edtBrochure.text = "No file"

                }

                binding.edtInterestedCategory.setText(profileBusinessData.interestedCategory)
                binding.edtInterestedSubcategory.setText(profileBusinessData.interestedSubCategory)

                /* Map View */

                Handler().postDelayed({
                    if (profileBusinessData.location?.coordinates?.isNotEmpty() == true) {
                        val long = profileBusinessData.location.coordinates[0]
                        val lat = profileBusinessData.location.coordinates[1]

                        if (lat != null && long != null) {
                            val latLng = LatLng(lat, long)
                            Const.b_lattitude = lat
                            Const.b_longitude = long
                            map?.addMarker(MarkerOptions().position(latLng))
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                        }
                    }
                }, 500)

            }
        }

        viewModel.profilePictureData.observe(this) { profilePictureData ->

            if (profilePictureData != null) {

                val profileImage = profilePictureData.s3Url + profilePictureData.key

                Glide.with(this@CreateBusinessProfileActivity)
                    .load(profileImage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.businessProfileImage)

            } else {
                Toast.makeText(this, "Something went wrong ", Toast.LENGTH_SHORT).show()
            }

        }

        viewModel.documentData.observe(this) { profilePictureData ->

            if (profilePictureData != null) {

                binding.rlUpload.visibility = View.GONE
                binding.rlAddBrochur.visibility = View.VISIBLE

                binding.edtBrochure.setOnClickListener {

                    val document = profilePictureData.s3Url + profilePictureData.key
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(document)))

                }

            } else {
                Toast.makeText(this, "Something went wrong ", Toast.LENGTH_SHORT).show()
            }

        }

        viewModel.createBusinessProfileData.observe(this) {

            if (it != null) {
                Snackbar.make(binding.linear, it.message.toString(), Snackbar.LENGTH_SHORT).show()
            }

            if (it?.status == 200) {
                startActivity(
                    Intent(
                        this@CreateBusinessProfileActivity,
                        ProductActivity::class.java
                    )
                )
            }

        }

    }

    private fun openImageOrDocument(id: Int) {
        if (IntentUtil.readPermission(
                this@CreateBusinessProfileActivity
            ) && IntentUtil.writePermission(
                this@CreateBusinessProfileActivity
            )
        ) {
            openIntent(id)
        } else
            onMediaPermission(id)
    }

    private fun openIntent(id: Int) {

        when (id) {
            R.id.edt_img -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "image/*"
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                startActivityForResult(intent, IntentUtil.IMAGE_PICKER_SELECT)
            }

            R.id.rl_upload, R.id.btn_change -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "application/pdf"
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(intent, IntentUtil.DOCUMENT_PICKER_SELECT)
            }
        }

    }

    private fun onMediaPermission(id: Int) {

        Dexter.withContext(this@CreateBusinessProfileActivity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                    if (permission?.areAllPermissionsGranted() == true) {
                        openIntent(id)
                    } else {
                        AppPermissionDialog.showPermission(
                            this@CreateBusinessProfileActivity,
                            getString(R.string.media_permission),
                            getString(R.string.media_permission_title)
                        )
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).withErrorListener {}

            .check()
    }

    @SuppressLint("Range", "Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntentUtil.IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {

            val uri = IntentUtil.getPickImageResultUri(baseContext, data)

            if (uri != null) {

                cropImage(uri)

            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {

                if (result != null) {
                    val mImageFile = result.uri.let { it ->
                        FileUtil.getPath(Uri.parse(it.toString()), this@CreateBusinessProfileActivity)
                            ?.let { File(it) }
                    }
                    val file = File(mImageFile.toString())
                    viewModel.setBusinessProfilePicture(file)
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(
                    this,
                    "Cropping failed" + result.error,
                    Toast.LENGTH_LONG
                ).show()

            }
        }

        if (requestCode == IntentUtil.DOCUMENT_PICKER_SELECT && resultCode == Activity.RESULT_OK) {

            val uri = data?.data
            if (uri != null) {

                if (uri.toString().startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor = contentResolver.query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            val displayName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            binding.edtBrochure.text = displayName
                        }
                    } finally {
                        cursor?.close();
                    }
                    val document =
                        FileUtil.getDocumentFilePath(this@CreateBusinessProfileActivity, uri)

                    val file = File(document)

                    viewModel.setBusinessBrochure(file)

                }

            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    override fun onResume() {
        super.onResume()
        if (Const.b_longitude != null && Const.b_lattitude != null) {
            if (map != null) {
                map?.clear()
                if (Const.mCurrLocationMarker != null) {
                    Const.mCurrLocationMarker.remove()
                }
                val userLocation = LatLng(Const.b_lattitude, Const.b_longitude)
                map?.addMarker(MarkerOptions().position(userLocation))
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
            }
        }
    }

    private fun cropImage(uri: Uri) {
        val mOptions = CropImageOptions()
        mOptions.allowFlipping = false
        mOptions.allowRotation = false
        mOptions.aspectRatioX = 1
        mOptions.aspectRatioY = 1
        mOptions.cropShape = CropImageView.CropShape.OVAL
        val intent = Intent()
        intent.setClass(this@CreateBusinessProfileActivity, CropImageActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE, uri)
        bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, mOptions)
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle)
        startActivityForResult(
            intent,
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
        )
    }

    private fun onLocationCheck(){
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
        } else {
            startActivity(
                Intent(
                    this@CreateBusinessProfileActivity,
                    MapsLocationActivity::class.java
                ).putExtra("isBusinessLocation", true)
            )
        }

    }

}