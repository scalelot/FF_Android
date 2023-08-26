package com.festum.festumfield.verstion.firstmodule.screens.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
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
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
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
        }else{
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
            startActivity(
                Intent(this@CreateBusinessProfileActivity, MapsLocationActivity::class.java).putExtra("isBusinessLocation", true)
            )
        }

        binding.btnNext.setOnClickListener {

            val businessProfile = CreateBusinessProfileModel(
                name = binding.edtBussinessName.text.toString(),
                category = binding.edtCategory.text.toString(),
                subCategory = binding.edtSubcategory.text.toString(),
                description =  binding.edtDescription.text.toString(),
                interestedCategory = binding.edtBussinessName.text.toString(),
                interestedSubCategory = binding.edtBussinessName.text.toString(),
                latitude = Const.lattitude,
                longitude = Const.longitude
            )

            viewModel.createBusinessProfile(businessProfile)

        }

    }

    @SuppressLint("SetTextI18n")
    override fun setupObservers() {

        viewModel.profileBusinessData.observe(this) { profileBusinessData ->

            if (profileBusinessData != null){

                Glide.with(this@CreateBusinessProfileActivity)
                    .load(Constans.Display_Image_URL + profileBusinessData.businessimage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.businessProfileImage)

                binding.edtBussinessName.setText(profileBusinessData.name)
                binding.edtCategory.setText(profileBusinessData.category)
                binding.edtSubcategory.setText(profileBusinessData.subCategory)
                binding.edtDescription.setText(profileBusinessData.description)

                if (profileBusinessData.brochure != null){

                    val brochure: String = profileBusinessData.brochure
                    val pdfName = brochure.substring(brochure.lastIndexOf('/') + 1)
                    binding.edtBrochure.text = pdfName
                    binding.edtBrochure.setOnClickListener {
                        val uri = Uri.parse(Constans.Display_Image_URL + profileBusinessData.brochure)
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

                        if (lat != null && long != null){
                            val latLng = LatLng(lat,long)
                            Const.b_lattitude = lat
                            Const.b_longitude = long
                            map?.addMarker(MarkerOptions().position(latLng))
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                        }
                    }
                },500)

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

            if (it?.status == 200){
                startActivity(Intent(this@CreateBusinessProfileActivity, ProductActivity::class.java))
            }

        }

    }

    private fun openImageOrDocument(id : Int){
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

        when(id){
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
                    if (permission?.areAllPermissionsGranted() == true){
                        openIntent(id)
                    } else {
                        AppPermissionDialog.showPermission(this@CreateBusinessProfileActivity,getString(R.string.media_permission),getString(R.string.media_permission_title))
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
                val mImageFile = uri.let { it ->
                    FileUtil.getPath(Uri.parse(it.toString()), this@CreateBusinessProfileActivity)
                        ?.let { File(it) }
                }
                val file = File(mImageFile.toString())
                viewModel.setBusinessProfilePicture(file)
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

}