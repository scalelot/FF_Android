package com.festum.festumfield.verstion.firstmodule.screens.main.group

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.databinding.ActivityCreateGroupBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.adapters.AddGroupMembersAdapter
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.screens.main.HomeActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CreateGroupBody
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.GroupInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.utils.FileUtil
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
import java.lang.reflect.Type


@AndroidEntryPoint
class CreateGroupActivity : BaseActivity<FriendsListViewModel>(), GroupInterface {

    private lateinit var binding: ActivityCreateGroupBinding
    private var membersList = arrayListOf<FriendsListItems>()
    private var addMemberAdapter: AddGroupMembersAdapter? = null
    private var profileKey: String = ""

    override fun getContentView(): View {
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun setupUi() {

        val memberList = intent.getStringExtra("memberList")

        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<FriendsListItems?>?>() {}.type
        val members: ArrayList<FriendsListItems> = gson.fromJson(memberList, type)

        membersList = members

        binding.txtParticipant.text = "Participants : " + membersList.size

        addMemberAdapter = AddGroupMembersAdapter(this@CreateGroupActivity, membersList, this)
        binding.recySelectedList.adapter = addMemberAdapter

        binding.cirGroupImage.setOnClickListener {
            if (AppPreferencesDelegates.get().userName.isBlank()) {

                createProfileDialog()

            } else {
                if (IntentUtil.readPermission(
                        this@CreateGroupActivity
                    ) && IntentUtil.writePermission(
                        this@CreateGroupActivity
                    )
                ) {
                    openIntent()
                } else
                    onMediaPermission()
            }
        }

        binding.ivDone.setOnClickListener {

            val membersIds = arrayListOf<String>()
            membersList.forEach {
                it.id?.let { it1 -> membersIds.add(it1) }
            }
            val createGroupBody = CreateGroupBody(
                members = membersIds,
                name = binding.etGroupTitle.text.toString(),
                description = binding.edtDescription.text.toString(),
                profileimage = profileKey,
            )

            if (createGroupBody.name.isNullOrEmpty()) {
                binding.etGroupTitle.requestFocus()
                binding.etGroupTitle.error = "Enter Group Name"
                Snackbar.make(binding.mainRelative, "Enter Group Name", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

//            if (createGroupBody.description.isNullOrEmpty()) {
//                binding.edtDescription.requestFocus()
//                binding.edtDescription.error = "Enter Description"
//                Snackbar.make(binding.mainRelative, "Enter Description", Snackbar.LENGTH_SHORT)
//                    .show()
//                return@setOnClickListener
//            }

            if (membersIds.isEmpty()) {
                Snackbar.make(binding.mainRelative, "Members not found", Snackbar.LENGTH_SHORT)
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

                Glide.with(this@CreateGroupActivity)
                    .load(profileImage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.cirGroupImage)

            } else {

                Toast.makeText(this, "Something went wrong ", Toast.LENGTH_SHORT).show()

            }

        }

        viewModel.createGroupData.observe(this) { createGroupData ->

            if (createGroupData?.members?.isNotEmpty() == true) {
                startActivity(
                    Intent(this@CreateGroupActivity, HomeActivity::class.java)
                )
            }

        }

    }



    override fun onAddMemberClick(
        items: FriendsListItems,
        b: Boolean,
        addMembersList: ArrayList<String>
    ) {

    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onRemoveMemberClick(items: FriendsListItems, position: Int) {

        membersList.remove(items)

        addMemberAdapter = AddGroupMembersAdapter(this@CreateGroupActivity, membersList, this)
        binding.recySelectedList.adapter = addMemberAdapter

        binding.txtParticipant.text = "Participants : " + membersList.size

    }

    override fun onMembersList(position: Int) {

    }

    override fun onRemoveMember(items: FriendsListItems) {

    }

    private fun onMediaPermission() {

        Dexter.withContext(this@CreateGroupActivity)
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
                            this@CreateGroupActivity,
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
        intent.setClass(this@CreateGroupActivity, CropImageActivity::class.java)
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
                        FileUtil.getPath(Uri.parse(it.toString()), this@CreateGroupActivity)
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