package com.festum.festumfield.verstion.firstmodule.screens.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.festum.festumfield.Activity.BusinessProfileActivity
import com.festum.festumfield.Activity.ProductActivity
import com.festum.festumfield.Activity.ProfileActivity
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ProfileActivityBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.adapters.ProfileViewPagerAdapter
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.utils.FileUtil
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class ProfilePreviewActivity : BaseActivity<ProfileViewModel>() {

    private lateinit var binding: ProfileActivityBinding
    private var businessProfile: Boolean? = null

    override fun getContentView(): View {
        binding = ProfileActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        /* Get Profile */
        viewModel.getProfile()

        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(resources.getText(R.string.tab_personal_info))
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(resources.getText(R.string.tab_business_info))
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(resources.getText(R.string.tab_reels))
        )

        binding.llBusinessProduct.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ProfilePreviewActivity, ProductActivity::class.java))
        })

        val item = binding.viewPager.currentItem

        if (item == 0){
            binding.btnEditProfile.setOnClickListener {
                val intent = Intent(this@ProfilePreviewActivity, ProfileActivity::class.java)
                intent.putExtra("EditProfile", resources.getString(R.string.edit_personal_profile))
                startActivity(intent)
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position

                when (tab.position) {
                    0 , 2 -> {
                        binding.btnEditProfile.text = resources.getString(R.string.edit_personal_profile)
                        binding.llBusinessProduct.visibility = View.GONE
                        binding.btnEditProfile.setOnClickListener {
                            val intent = Intent(this@ProfilePreviewActivity, ProfileActivity::class.java)
                            intent.putExtra("EditProfile", resources.getString(R.string.edit_personal_profile))
                            startActivity(intent)
                        }
                    }
                    1 -> {
                        binding.btnEditProfile.text =
                            resources.getString(R.string.edit_business_profile)
                        if (businessProfile == true) {
                            binding.llBusinessProduct.visibility = View.VISIBLE
                        } else {
                            binding.llBusinessProduct.visibility = View.GONE
                        }
                        binding.btnEditProfile.setOnClickListener {
                            val intent = Intent(this@ProfilePreviewActivity, BusinessProfileActivity::class.java)
                            intent.putExtra("EditProfile", resources.getString(R.string.edit_business_profile))
                            startActivity(intent)
                        }
                    }

                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {}

        })

        binding.icBack.setOnClickListener {
            finish()
        }

        /* Profile Picture Set */
        binding.editImg.setOnClickListener {
            if (IntentUtil.readPermission(
                    this@ProfilePreviewActivity
                ) && IntentUtil.writePermission(
                    this@ProfilePreviewActivity
                )
            ) {
                openIntent()
            } else
                onMediaPermission()
        }


    }

    override fun setupObservers() {

        viewModel.profileData.observe(this) { profileData ->

            if (profileData != null) {

                Glide.with(this@ProfilePreviewActivity)
                    .load(Constans.Display_Image_URL + profileData.profileimage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.userProfileImage)

                binding.uName.text = profileData.fullName ?: "Festum Field"
                binding.uNickname.text = profileData.nickName ?: "Festum"

                businessProfile = profileData.isBusinessProfileCreated

                if (profileData.socialMediaLinks != null) {

                    profileData.socialMediaLinks.forEach {

                        when (it?.platform) {
                            "Facebook" -> {
                                binding.icFb.visibility = View.VISIBLE
                            }
                            "Instagram" -> {
                                binding.icInsta.visibility = View.VISIBLE
                            }
                            "Twitter" -> {
                                binding.icTwitter.visibility = View.VISIBLE
                            }
                            "Linkedin" -> {
                                binding.icLinkdin.visibility = View.VISIBLE
                            }
                            "Pinterest" -> {
                                binding.icPinterest.visibility = View.VISIBLE
                            }
                            "Youtube" -> {
                                binding.icYoutube.visibility = View.VISIBLE
                            }
                        }
                    }

                }

                /* Set View Pager */
                binding.viewPager.adapter = ProfileViewPagerAdapter(profileData,supportFragmentManager)

            }

        }

        viewModel.profilePictureData.observe(this) { profilePictureData ->

            if (profilePictureData != null) {

                val profileImage = profilePictureData.s3Url + profilePictureData.key

                Glide.with(this@ProfilePreviewActivity)
                    .load(profileImage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.userProfileImage)

            } else {
                Toast.makeText(this, "Something went wrong ", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun onMediaPermission() {

        Dexter.withContext(this@ProfilePreviewActivity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                    if (permission?.areAllPermissionsGranted() == true){
                        openIntent()
                    } else {
                        AppPermissionDialog.showPermission(this@ProfilePreviewActivity,getString(R.string.media_permission),getString(R.string.media_permission_title))
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

    fun openIntent() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
        startActivityForResult(intent, IntentUtil.IMAGE_PICKER_SELECT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntentUtil.IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {

            val uri = IntentUtil.getPickImageResultUri(baseContext, data)

            if (uri != null) {
                val mImageFile = uri.let { it ->
                    FileUtil.getPath(Uri.parse(it.toString()), this@ProfilePreviewActivity)
                        ?.let { File(it) }
                }
                val file = File(mImageFile.toString())
                viewModel.setProfilePicture(file)
            }

        }
    }

}