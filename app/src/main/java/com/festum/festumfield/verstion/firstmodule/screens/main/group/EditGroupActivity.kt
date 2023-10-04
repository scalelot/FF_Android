package com.festum.festumfield.verstion.firstmodule.screens.main.group

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ActivityEditGroupBinding
import com.festum.festumfield.databinding.ActivityNewGroupBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.adapters.GroupMembersListAdapter
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.screens.main.HomeActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CreateGroupBody
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.utils.FileUtil
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
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
class EditGroupActivity : BaseActivity<FriendsListViewModel>() {

    private lateinit var binding: ActivityEditGroupBinding
    private lateinit var membersList: FriendsListItems
    private var friendsListAdapter: GroupMembersListAdapter? = null
    private val groupMembers = arrayListOf<FriendsListItems>()
    private var profileKey: String = ""

    override fun getContentView(): View {
        binding = ActivityEditGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        val intent = intent.extras

        val jsonList = intent?.getString("MembersList")

        membersList = Gson().fromJson(jsonList, FriendsListItems::class.java)

        val profileImage = Constans.Display_Image_URL + membersList.profileimage

        binding.txtTitle.text = membersList.name
        binding.edtName.setText(membersList.name)
        binding.edtDescription.setText(membersList.description)

        Glide.with(this@EditGroupActivity)
            .load(profileImage)
            .placeholder(R.drawable.ic_user)
            .into(binding.groupProfileImage)

        binding.icBack.setOnClickListener {
            finish()
        }

        binding.editImg.setOnClickListener {
            if (AppPreferencesDelegates.get().userName.isBlank()) {

                createProfileDialog()

            } else {
                if (IntentUtil.readPermission(
                        this@EditGroupActivity
                    ) && IntentUtil.writePermission(
                        this@EditGroupActivity
                    )
                ) {
                    openIntent()
                } else
                    onMediaPermission()
            }
        }

        binding.btnSave.setOnClickListener {

            val membersIds = arrayListOf<String>()
            membersList.members?.forEach {
                it.membersList?.id?.let { it1 -> membersIds.add(it1) }
            }
            val createGroupBody = CreateGroupBody(
                groupid = membersList.id,
                members = membersIds,
                name = binding.edtName.text.toString(),
                description = binding.edtDescription.text.toString(),
                profileimage = profileKey,
            )

            if (createGroupBody.name.isNullOrEmpty()) {
                binding.edtName.requestFocus()
                binding.edtName.error = "Enter Group Name"
                Snackbar.make(binding.relative, "Enter Group Name", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (createGroupBody.description.isNullOrEmpty()) {
                binding.edtDescription.requestFocus()
                binding.edtDescription.error = "Enter Description"
                Snackbar.make(binding.relative, "Enter Description", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (membersIds.isEmpty()) {
                Snackbar.make(binding.relative, "Members not found", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            viewModel.createGroup(createGroupBody)

        }

    }

    override fun setupObservers() {

        viewModel.groupProfilePictureData.observe(this) { groupProfilePictureData ->

            if (groupProfilePictureData != null) {

                val profileImage = groupProfilePictureData.s3Url + groupProfilePictureData.key

                profileKey = groupProfilePictureData.key.toString()

                Glide.with(this@EditGroupActivity)
                    .load(profileImage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.groupProfileImage)

            } else {

                Toast.makeText(this, "Something went wrong ", Toast.LENGTH_SHORT).show()

            }

        }

        viewModel.createGroupData.observe(this) { createGroupData ->

            if (createGroupData != null) {

                startActivity(
                    Intent(this@EditGroupActivity, HomeActivity::class.java)
                )

            }

        }

    }

    private fun onMediaPermission() {

        Dexter.withContext(this@EditGroupActivity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                    if (permission?.areAllPermissionsGranted() == true) {
                        openIntent()
                    } else {
                        AppPermissionDialog.showPermission(
                            this@EditGroupActivity,
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


    private fun openIntent() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
        startActivityForResult(intent, IntentUtil.IMAGE_PICKER_SELECT)
    }

    private fun createProfileDialog() {

        MaterialAlertDialogBuilder(
            this,
            R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog
        )
            .setTitle(getString(R.string.create_profile))
            .setMessage(getString(R.string.crate_profile))
            .setPositiveButton("OK") { dialogInterface, i ->
                dialogInterface.dismiss()

            }
            .show()
    }

    private fun cropImage(uri: Uri) {
        val mOptions = CropImageOptions()
        mOptions.allowFlipping = false
        mOptions.allowRotation = false
        mOptions.aspectRatioX = 1
        mOptions.aspectRatioY = 1
        mOptions.cropShape = CropImageView.CropShape.OVAL
        val intent = Intent()
        intent.setClass(this@EditGroupActivity, CropImageActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE, uri)
        bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, mOptions)
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle)
        startActivityForResult(
            intent,
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
        )
    }

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
                        FileUtil.getPath(Uri.parse(it.toString()), this@EditGroupActivity)
                            ?.let { File(it) }
                    }
                    val file = File(mImageFile.toString())
                    viewModel.setGroupProfilePicture(file)
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(
                    this,
                    "Cropping failed" + result.error,
                    Toast.LENGTH_LONG
                ).show()

            }
        }

    }
}