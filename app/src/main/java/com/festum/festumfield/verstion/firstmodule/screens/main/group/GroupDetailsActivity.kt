package com.festum.festumfield.verstion.firstmodule.screens.main.group

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ActivityGroupDetailsBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.adapters.EditGroupMembersListAdapter
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AddMembersDialog
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.screens.dialog.RemoveMembersDialog
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CreateGroupBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.GroupOneBody
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.GroupInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.GroupMembersInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.BusinessProfile
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.GroupListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.GroupMembersListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.GroupMembersMessageItem
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.MembersList
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.SocialMediaLinksItem
import com.festum.festumfield.verstion.firstmodule.utils.FileUtil
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
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
class GroupDetailsActivity : BaseActivity<FriendsListViewModel>(), GroupMembersInterface {

    private lateinit var binding: ActivityGroupDetailsBinding
    private lateinit var groupOneItem: GroupMembersListItems
    private lateinit var addMemberList: GroupMembersListItems
    private var friendsListAdapter: EditGroupMembersListAdapter? = null
    private val groupMembers = arrayListOf<FriendsListItems>()
    private val addGroupMembers = arrayListOf<GroupMembersListItems>()
    private var removeMembersList = arrayListOf<String>()
    private var profileKey: String = ""
    private var removeUserID: String = ""

    override fun getContentView(): View {
        binding = ActivityGroupDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun setupUi() {

        val intent = intent.extras

        val jsonList = intent?.getString("groupMembersList")


        groupOneItem = Gson().fromJson(jsonList, GroupMembersListItems::class.java)

        val profileImage = Constans.Display_Image_URL + groupOneItem.profileimage

        binding.description.text = groupOneItem.description

        Glide.with(this@GroupDetailsActivity)
            .load(profileImage)
            .placeholder(R.drawable.ic_user)
            .into(binding.groupProfileImage)

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.editImg.setOnClickListener {
            if (AppPreferencesDelegates.get().userName.isBlank()) {

                createProfileDialog()

            } else {
                if (IntentUtil.readPermission(
                        this@GroupDetailsActivity
                    ) && IntentUtil.writePermission(
                        this@GroupDetailsActivity
                    )
                ) {
                    openIntent()
                } else
                    onMediaPermission()
            }
        }

        binding.btnGroupInfo.setOnClickListener {

            val intent = Intent(this@GroupDetailsActivity, EditGroupActivity::class.java)
            val jsonItem = Gson().toJson(groupOneItem)
            intent.putExtra("MembersList", jsonItem)
            startActivity(intent)

        }

        binding.llAddUser.setOnClickListener {

            val dialog = AddMembersDialog(addMemberList, this,  removeMembersList)
            dialog.show(supportFragmentManager, "AddMembersDialog")

        }

        viewModel.getGroup(GroupOneBody(groupOneItem.id))

    }

    @SuppressLint("SetTextI18n")
    override fun setupObservers() {


        viewModel.groupProfilePictureData.observe(this) { groupProfilePictureData ->

            if (groupProfilePictureData != null) {

                val profileImage = groupProfilePictureData.s3Url + groupProfilePictureData.key

                profileKey = groupProfilePictureData.key.toString()

                Glide.with(this@GroupDetailsActivity)
                    .load(profileImage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.groupProfileImage)

                val createGroupBody = CreateGroupBody(
                    groupid = groupOneItem.id,
                    profileimage = profileKey
                )

                viewModel.createGroup(createGroupBody)

            } else {

                Toast.makeText(this, "Something went wrong ", Toast.LENGTH_SHORT).show()

            }

        }

        viewModel.removeMembersData.observe(this){

            if (it != null){

                friendsListAdapter?.updateOffline(it)

            }else{

                Toast.makeText(this@GroupDetailsActivity, "Something get wrong", Toast.LENGTH_SHORT).show()
            }

        }


        viewModel.groupsOneData.observe(this) { groupsOneData ->


            if (lifecycle.currentState == Lifecycle.State.RESUMED) {

                if (groupsOneData != null) {

                    addMemberList = GroupMembersListItems(
                      aboutUs= groupsOneData.aboutUs,
                      isSender=groupsOneData.isSender,
                      isPinned=groupsOneData.isPinned,
                      gender=groupsOneData.gender,
                      nickName=groupsOneData.nickName,
                      fullName=groupsOneData.fullName,
                      name=groupsOneData.name,
                      emailId=groupsOneData.emailId,
                      lastMessage=GroupMembersMessageItem(
                          message = groupsOneData.lastMessage?.message?.id,
                          timestamp = groupsOneData.timestamp
                      ),
                      userName=groupsOneData.userName,
                      profileimage=groupsOneData.profileimage,
                      businessprofile = groupsOneData.businessprofile,
                      areaRange = groupsOneData.areaRange,
                      createdAt=groupsOneData.createdAt,
                      isBusinessProfileCreated=groupsOneData.isBusinessProfileCreated,
                      hobbies = groupsOneData.hobbies,
                      contactNo=groupsOneData.contactNo,
                      dob=groupsOneData.dob,
                      description=groupsOneData.description,
                      socialMediaLinks =groupsOneData.socialMediaLinks,
                      id=groupsOneData.id,
                      interestedin=groupsOneData.interestedin,
                      status=groupsOneData.status,
                      updatedAt=groupsOneData.updatedAt,
                      timestamp=groupsOneData.timestamp,
                      isNewMessage=groupsOneData.isNewMessage,
                      messageSize=groupsOneData.messageSize,
                      channelID=groupsOneData.channelID,
                      online=groupsOneData.online,
                      members  =groupsOneData.members ,
                      unreadMessageCount =groupsOneData.unreadMessageCount,
                    )

                    groupsOneData.members?.forEach {

                        val members = GroupMembersListItems(
                            profileimage = it.membersList?.profileimage,
                            fullName = it.membersList?.fullName,
                            aboutUs = it.membersList?.aboutUs,
                            id = it.membersList?.id
                        )

                        it.membersList?.id?.let { it1 -> removeMembersList.add(it1) }
                        addGroupMembers.add(members)

                    }

                    binding.groupName.text = groupsOneData.name

                    binding.txtPeople.text = groupsOneData.members?.size.toString() + " peoples"

                    Handler(Looper.getMainLooper()).postDelayed({
                        friendsListAdapter = EditGroupMembersListAdapter(this@GroupDetailsActivity, addGroupMembers, this)
                        binding.recyclerGroup.adapter = friendsListAdapter

                    },100)

                }

            }

        }

    }

    private fun onMediaPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            Dexter.withContext(this@GroupDetailsActivity)
                .withPermissions(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {
                            openIntent()
                        } else {
                            AppPermissionDialog.showPermission(
                                this@GroupDetailsActivity,
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

        } else {

            Dexter.withContext(this@GroupDetailsActivity)
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
                                this@GroupDetailsActivity,
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
        intent.setClass(this@GroupDetailsActivity, CropImageActivity::class.java)
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
                        FileUtil.getPath(Uri.parse(it.toString()), this@GroupDetailsActivity)
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




    @SuppressLint("SetTextI18n")
    override fun onMembersList(position: Int) {

        binding.txtPeople.text = "$position peoples"

    }

    override fun onRemoveMember(items: GroupMembersListItems) {

        val membersIds = arrayListOf<String>()

        membersIds.add(items.id.toString())

        Log.e("TAG", "onRemoveMember:---- $items")

        val removeMembers = CreateGroupBody(
            groupid = groupOneItem.id,
            members = membersIds
        )

        removeMembersList.remove(items.id)
        removeUserID = items.id.toString()

        viewModel.removeMembers(removeMembers)

    }


    override fun onAddMemberClick(
        items: GroupMembersListItems,
        b: Boolean,
        addMembersList: ArrayList<String>
    ) {

        addGroupMembers.clear()
        removeMembersList.clear()

        removeMembersList.addAll(addMembersList)

        viewModel.getGroup(GroupOneBody(groupOneItem.id))

    }

    override fun onRemoveMemberClick(items: GroupMembersListItems, position: Int) {

        val dialog = RemoveMembersDialog(items, this)
        dialog.show(supportFragmentManager, "RemoveMembersDialog")

    }
}