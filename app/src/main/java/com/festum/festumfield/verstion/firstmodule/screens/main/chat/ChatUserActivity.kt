package com.festum.festumfield.verstion.firstmodule.screens.main.chat

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ActivityChatUserBinding
import com.festum.festumfield.databinding.ActivityVideoCallBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.adapters.ChatUserViewPagerAdapter
import com.festum.festumfield.verstion.firstmodule.screens.adapters.ProfileViewPagerAdapter
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.screens.main.profile.CreateBusinessProfileActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.profile.CreatePersonProfileActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.AppGroupVideoCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.AppVideoCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.WebAudioCallingActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ChatUserBody
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ChatUserResponse
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.GroupMembersListItems
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject

@AndroidEntryPoint
class ChatUserActivity : BaseActivity<FriendsListViewModel>() {

    private lateinit var binding: ActivityChatUserBinding

    private var friendsItem: FriendsListItems? = null
    private lateinit var upComingCallBinding: ActivityVideoCallBinding
    private var upComingCallDialog: Dialog? = null

    private var isVideoCalling = false
    private var isCallStart = false
    private var isAudioCalling = false
    private var isCallAccpeted = false

    private var callId: String? = null

    override fun getContentView(): View {

        binding = ActivityChatUserBinding.inflate(layoutInflater)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun setupUi() {

        val intent = intent.extras
        val jsonList = intent?.getString("friendList")
        friendsItem = Gson().fromJson(jsonList, FriendsListItems::class.java)

        viewModel.getOneFriend(ChatUserBody(friendid = friendsItem?.id))

        binding.chatTabLayout.addTab(
            binding.chatTabLayout.newTab().setText(resources.getText(R.string.tab_personal_info))
        )
        binding.chatTabLayout.addTab(
            binding.chatTabLayout.newTab().setText(resources.getText(R.string.tab_business_info))
        )

        binding.chatTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position

                when (tab.position) {

                    0 -> {}

                    1 -> {}

                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}

        })

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.chatTabLayout.selectTab(binding.chatTabLayout.getTabAt(position))
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        binding.icBack.setOnClickListener {
            finish()
        }
        
        binding.llVideo.setOnClickListener{

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                onVideoCallPermission(true)
            } else {
                onVideoCallPermission(false)
            }
            
        }

        binding.llAudio.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                onAudioCallPermission(true)
            } else {
                onAudioCallPermission(false)
            }

        }

    }

    override fun setupObservers() {

        viewModel.getFriendData.observe(this) { getFriendData ->

            if (getFriendData != null){

                Glide.with(this@ChatUserActivity)
                    .load(Constans.Display_Image_URL + getFriendData.profileimage)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.userProfileImage)

                binding.chatName.text = getFriendData.fullName ?: ""
                binding.uNickname.text = getFriendData.nickName ?: ""

                if (getFriendData.socialMediaLinks != null) {

                    getFriendData.socialMediaLinks.forEach {

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
                binding.viewPager.adapter = ChatUserViewPagerAdapter(getFriendData, supportFragmentManager)

            }

        }

        viewModel.callStartData.observe(this){ callStartData ->

            callId = callStartData?.id.toString()
            
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onVideoCallPermission(isTiramisu: Boolean) {

        if (isTiramisu){
            Dexter.withContext(this@ChatUserActivity)
                .withPermissions(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {

                            val message = JSONObject().apply {

                                val jsonArray = JSONArray()
                                jsonArray.put(friendsItem?.id?.lowercase())
                                jsonArray.put(AppPreferencesDelegates.get().channelId.lowercase())
                                put("memberIds", jsonArray)
                                put("fromId", AppPreferencesDelegates.get().channelId.lowercase())
                                put("name", AppPreferencesDelegates.get().userName)
                                put("isVideoCall", true)
                                put("isCallingFromApp", true)
                                put("isGroupCalling", false)

                            }

                            SocketManager.mSocket?.emit("callUser", message)

                            /* Call Start */

                            viewModel.callStart(from = AppPreferencesDelegates.get().channelId.lowercase(),to = friendsItem?.id?.lowercase(),true,false,"")

                            upComingCallView(friendsItem)

                            isVideoCalling = true
                            isCallStart = true

                        } else {
                            AppPermissionDialog.showPermission(
                                this@ChatUserActivity,
                                getString(R.string.request_camera_mic_permissions_text),
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
            Dexter.withContext(this@ChatUserActivity)
                .withPermissions(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {

                            val message = JSONObject().apply {

                                val jsonArray = JSONArray()
                                jsonArray.put(friendsItem?.id?.lowercase())
                                jsonArray.put(AppPreferencesDelegates.get().channelId.lowercase())
                                put("memberIds", jsonArray)
                                put("fromId", AppPreferencesDelegates.get().channelId.lowercase())
                                put("name", AppPreferencesDelegates.get().userName)
                                put("isVideoCall", true)
                                put("isCallingFromApp", true)
                                put("isGroupCalling", false)

                            }

                            SocketManager.mSocket?.emit("callUser", message)

                            /* Call Start */

                            viewModel.callStart(from = AppPreferencesDelegates.get().channelId.lowercase(),to = friendsItem?.id?.lowercase(),true,false,"")

                            upComingCallView(friendsItem)

                            isVideoCalling = true
                            isCallStart = true

                        } else {
                            AppPermissionDialog.showPermission(
                                this@ChatUserActivity,
                                getString(R.string.request_camera_mic_permissions_text),
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onAudioCallPermission(isTiramisu: Boolean) {

        if (isTiramisu){

            Dexter.withContext(this@ChatUserActivity)
                .withPermissions(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {

                            val message = JSONObject().apply {

                                val jsonArray = JSONArray()
                                jsonArray.put(friendsItem?.id?.lowercase())
                                jsonArray.put(AppPreferencesDelegates.get().channelId.lowercase())
                                put("memberIds", jsonArray)
                                put("fromId", AppPreferencesDelegates.get().channelId.lowercase())
                                put("name", AppPreferencesDelegates.get().userName)
                                put("isVideoCall", false)
                                put("isCallingFromApp", true)
                                put("isGroupCalling", false)

                            }

                            SocketManager.mSocket?.emit("callUser", message)

                            upComingCallView(friendsItem)

                            /* Call Start */
                            viewModel.callStart(from = AppPreferencesDelegates.get().channelId.lowercase(),to = friendsItem?.id?.lowercase(),false,false,"")

                            isAudioCalling = true
                            isCallStart = true

                        } else {
                            AppPermissionDialog.showPermission(
                                this@ChatUserActivity,
                                getString(R.string.request_mic_permissions_text),
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

            Dexter.withContext(this@ChatUserActivity)
                .withPermissions(Manifest.permission.RECORD_AUDIO)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {

                            val message = JSONObject().apply {

                                val jsonArray = JSONArray()
                                jsonArray.put(friendsItem?.id?.lowercase())
                                jsonArray.put(AppPreferencesDelegates.get().channelId.lowercase())
                                put("memberIds", jsonArray)
                                put("fromId", AppPreferencesDelegates.get().channelId.lowercase())
                                put("name", AppPreferencesDelegates.get().userName)
                                put("isVideoCall", false)
                                put("isCallingFromApp", true)
                                put("isGroupCalling", false)

                            }

                            SocketManager.mSocket?.emit("callUser", message)

                            upComingCallView(friendsItem)

                            /* Call Start */
                            viewModel.callStart(from = AppPreferencesDelegates.get().channelId.lowercase(),to =friendsItem?.id?.lowercase(),false,false,"")

                            isAudioCalling = true
                            isCallStart = true

                        } else {
                            AppPermissionDialog.showPermission(
                                this@ChatUserActivity,
                                getString(R.string.request_mic_permissions_text),
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

    private fun upComingCallView(
        upComingCallUser: FriendsListItems?,
    ) {

        upComingCallBinding = ActivityVideoCallBinding.inflate(layoutInflater)

        upComingCallDialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        upComingCallDialog?.setContentView(upComingCallBinding.root)

        Glide.with(this@ChatUserActivity)
            .load(Constans.Display_Image_URL + upComingCallUser?.profileimage)
            .placeholder(R.drawable.ic_user_img).into(upComingCallBinding.videocallImage)

        upComingCallBinding.llVideoCall.visibility = View.GONE
        upComingCallBinding.llMute.visibility = View.GONE

        upComingCallBinding.videocallUsername.text = upComingCallUser?.fullName

        upComingCallBinding.llCallCut.setOnClickListener {

            val jsonObj = JSONObject()
            jsonObj.put("id", upComingCallUser?.id)
            SocketManager.mSocket?.emit("endCall", jsonObj)
            upComingCallDialog?.dismiss()

        }

        upComingCallDialog?.show()

    }
    
    fun getMessage(){

        SocketManager.mSocket?.on("webrtcMessage") { args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "webrtcMessage:---+++++-- $data")

            if (isVideoCalling) {
                val i = Intent(this@ChatUserActivity, AppVideoCallingActivity::class.java)
                i.putExtra("remoteChannelId", friendsItem?.id?.lowercase())
                i.putExtra("remoteChannelId", friendsItem?.id?.lowercase())
                i.putExtra("remoteUser", friendsItem?.fullName)
                i.putExtra("callId", callId)
                startActivityForResult(i, IntentUtil.IS_VIDEO_CALLING)
                isVideoCalling = false
                isCallAccpeted = true

                Handler(Looper.getMainLooper()).postDelayed({ upComingCallDialog?.dismiss() }, 1000)

            }

            if (isAudioCalling) {

                val i = Intent(this@ChatUserActivity, WebAudioCallingActivity::class.java)
                i.putExtra("remoteChannelId", friendsItem?.id?.lowercase())
                i.putExtra("remoteChannelId", friendsItem?.id?.lowercase())
                i.putExtra("remoteUser", friendsItem?.fullName)
                i.putExtra("callId", callId)
                startActivityForResult(i, IntentUtil.IS_AUDIO_CALLING)
                isAudioCalling = false
                isCallAccpeted = true

                Handler(Looper.getMainLooper()).postDelayed({ upComingCallDialog?.dismiss() }, 1000)

            }


        }?.on("endCall") { args ->

            try {
                val data = args[0] as JSONObject
                upComingCallDialog?.dismiss()
                AppPreferencesDelegates.get().isVideoCalling = true
                AppPreferencesDelegates.get().isAudioCalling = true

                /* Call End */
                viewModel.callEnd(callId)

            } catch (e: Exception) {
                upComingCallDialog?.dismiss()
            }

        }
        
    }

}