package com.festum.festumfield.verstion.firstmodule.screens.main.chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ActivityIncomingCallBinding
import com.festum.festumfield.databinding.ChatActivityBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.screens.fragment.FriendsListFragment.Companion.friendsListItems
import com.festum.festumfield.verstion.firstmodule.screens.fragment.FriendsListFragment.Companion.groupsListItems
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.AppAudioCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.AppGroupVideoCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.AppVideoCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.WebAudioCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.WebVideoCallingActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.GroupListItems
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
@AndroidEntryPoint
class IncomingCallActivity : BaseActivity<ChatViewModel>() {

    private lateinit var binding: ActivityIncomingCallBinding
    private var upComingCallUser: FriendsListItems? = null
    private var upComingGroupCallUser: GroupListItems? = null

    private var groupId: String = ""
    private var mMediaPlayer: MediaPlayer = MediaPlayer()

    override fun getContentView(): View {

        binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun setupUi() {

        val jsonString = intent?.getStringExtra("jsonData").toString()
        val jsonObject = JSONObject(jsonString)

        val signal = jsonObject.optJSONObject("signal")
        val type = signal?.optString("type")
        val sdp = signal?.optString("sdp")
        val from = jsonObject.optString("fromId")
        val name = jsonObject.optString("name")
//                val isVideoCall = data.optBoolean("channelID")
        val isVideoCall = jsonObject.optBoolean("isVideoCall")
        val isCallingFromApp = jsonObject.optBoolean("isCallingFromApp")
        val isGroupCalling = jsonObject.optBoolean("isGroupCall")
        val groupId = jsonObject.optString("groupId")

        if (isGroupCalling){

            groupsListItems?.forEach {

                if (it.id?.contains(groupId.lowercase()) == true) {
                    upComingGroupCallUser = it
                } else{
                    Log.e("TAG", "setupUi:--- " + "Open" )
                }

            }

            Glide.with(this@IncomingCallActivity)
                .load(Constans.Display_Image_URL + upComingGroupCallUser?.profileimage)
                .placeholder(R.drawable.ic_user).into(binding.upcomingcallUserImg)

            binding.upcomingUsername.text = name

        } else{

            friendsListItems?.forEach {

                if (it.id?.contains(from.toString().lowercase()) == true) {
                    upComingCallUser = it
                }

            }

            Glide.with(this@IncomingCallActivity)
                .load(Constans.Display_Image_URL + upComingCallUser?.profileimage)
                .placeholder(R.drawable.ic_user).into(binding.upcomingcallUserImg)

            binding.upcomingUsername.text = name

        }

        playAudio(this@IncomingCallActivity, "skype")

        binding.llCallCut.setOnClickListener {

            val jsonObj = JSONObject()
            jsonObj.put("id", from.lowercase())
            SocketManager.mSocket?.emit("endCall", jsonObj)
            stopAudio()
            finish()

        }

        binding.llCallRecive.setOnClickListener {

            if (isVideoCall) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onCameraPermission(
                        isCallingFromApp,
                        from.lowercase(),
                        name,
                        true,
                        isGroupCalling,
                        upComingCallUser,
                        upComingGroupCallUser,
                        groupId
                    )
                } else {

                    onCameraPermission(
                        isCallingFromApp,
                        from.lowercase(),
                        name,
                        false,
                        isGroupCalling,
                        upComingCallUser,
                        upComingGroupCallUser,
                        groupId
                    )
                }

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onAudioPermission(
                        isCallingFromApp,
                        from.lowercase(),
                        name,
                        upComingCallUser?.profileimage,
                        true
                    )
                } else {
                    onAudioPermission(
                        isCallingFromApp,
                        from.lowercase(),
                        name,
                        upComingCallUser?.profileimage,
                        false
                    )
                }

            }

            Log.e("TAG", "setupUi:------- " + AppPreferencesDelegates.get().isCallId )

            viewModel.callAccept(AppPreferencesDelegates.get().isCallId)

        }

        SocketManager.mSocket?.on("endCall") { args ->

            try {

                val data = args[0] as JSONObject

                Log.e("TAG", "callUser:---- $data")

                Handler(Looper.getMainLooper()).postDelayed({

                    stopAudio()
                    finish()

                }, 500)

                // Call End
                viewModel.callEnd(AppPreferencesDelegates.get().isCallId)

            } catch (e: Exception) {

                stopAudio()
                finish()

            }

        }

    }


    override fun setupObservers() {

    }

    @SuppressLint("DiscouragedApi")
    private fun playAudio(mContext: Context, fileName: String) {
        try {
            mMediaPlayer = MediaPlayer.create(
                mContext,
                mContext.resources.getIdentifier(fileName, "raw", mContext.packageName)
            )
            mMediaPlayer.start()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun stopAudio() {

        try {

            mMediaPlayer.release()
            mMediaPlayer.pause()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onCameraPermission(
        isCallingFromApp: Boolean,
        remoteChannelId: String,
        name: String,
        isTiramisu: Boolean,
        isGroupCalling: Boolean,
        upComingCallUser: FriendsListItems?,
        upComingGroupCallUser: GroupListItems?,
        groupId: String?
    ) {

        if (isTiramisu) {
            Dexter.withContext(this@IncomingCallActivity)
                .withPermissions(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {

                        if (permission?.areAllPermissionsGranted() == true) {

                            if (isCallingFromApp && isGroupCalling) {

                                val intent = Intent(
                                    this@IncomingCallActivity,
                                    AppGroupVideoCallingActivity::class.java
                                )
                                val jsonItem = Gson().toJson(upComingGroupCallUser)
                                intent.putExtra("groupList", jsonItem)
                                intent.putExtra("groupId", groupId)
                                startActivity(intent)
                                stopAudio()
                                finish()

                                Log.e("TAG", "onPermissionsCheckedModel:---- $jsonItem")
                                Log.e("TAG", "onPermissionsCheckedGroupId:---- $groupId")

                            } else {
                                if (isCallingFromApp) {
                                    val intent =
                                        Intent(
                                            this@IncomingCallActivity,
                                            AppVideoCallingActivity::class.java
                                        )
                                    intent.putExtra("remoteChannelId", remoteChannelId)
                                    intent.putExtra("remoteUser", name)
                                    intent.putExtra("callReceive", true)
                                    startActivity(intent)
                                    stopAudio()
                                    finish()
                                } else {

                                    val intent =
                                        Intent(
                                            this@IncomingCallActivity,
                                            WebVideoCallingActivity::class.java
                                        )
                                    intent.putExtra("remoteChannelId", remoteChannelId)
                                    intent.putExtra("remoteUser", name)
                                    intent.putExtra("callReceive", true)
                                    startActivity(intent)
                                    stopAudio()
                                    finish()
                                }

                            }

                        } else {
                            AppPermissionDialog.showPermission(
                                this@IncomingCallActivity,
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
            Dexter.withContext(this@IncomingCallActivity)
                .withPermissions(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {

                            if (isCallingFromApp && isGroupCalling) {

                                val intent = Intent(
                                    this@IncomingCallActivity,
                                    AppGroupVideoCallingActivity::class.java
                                )
                                val jsonItem = Gson().toJson(upComingGroupCallUser)
                                intent.putExtra("groupList", jsonItem)
                                intent.putExtra("groupId", groupId)
                                startActivity(intent)
                                stopAudio()
                                finish()

                                Log.e("TAG", "onPermissionsCheckedModel:---- $jsonItem")
                                Log.e("TAG", "onPermissionsCheckedGroupId:---- $groupId")

                            } else {
                                if (isCallingFromApp) {
                                    val intent =
                                        Intent(
                                            this@IncomingCallActivity,
                                            AppVideoCallingActivity::class.java
                                        )
                                    intent.putExtra("remoteChannelId", remoteChannelId)
                                    intent.putExtra("remoteUser", name)
                                    intent.putExtra("callReceive", true)
                                    startActivity(intent)
                                    stopAudio()
                                    finish()
                                } else {

                                    val intent =
                                        Intent(
                                            this@IncomingCallActivity,
                                            WebVideoCallingActivity::class.java
                                        )
                                    intent.putExtra("remoteChannelId", remoteChannelId)
                                    intent.putExtra("remoteUser", name)
                                    intent.putExtra("callReceive", true)
                                    startActivity(intent)
                                    stopAudio()
                                    finish()
                                }

                            }

                        } else {
                            AppPermissionDialog.showPermission(
                                this@IncomingCallActivity,
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
    private fun onAudioPermission(
        isCallingFromApp: Boolean,
        remoteChannelId: String,
        name: String,
        profileimage: String?,
        isTiramisu: Boolean
    ) {

        if (isTiramisu) {

            Dexter.withContext(this@IncomingCallActivity)
                .withPermissions(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {

                            if (isCallingFromApp) {
                                val intent =
                                    Intent(this@IncomingCallActivity, AppAudioCallingActivity::class.java)
                                intent.putExtra("remoteChannelId", remoteChannelId)
                                intent.putExtra("remoteUser", name)
                                intent.putExtra("callReceive", true)
                                intent.putExtra("profileImage", profileimage)
                                startActivity(intent)
                                stopAudio()
                                finish()
                            } else {
                                val intent =
                                    Intent(this@IncomingCallActivity, WebAudioCallingActivity::class.java)
                                intent.putExtra("remoteChannelId", remoteChannelId)
                                intent.putExtra("remoteUser", name)
                                intent.putExtra("callReceive", true)
                                intent.putExtra("profileImage", profileimage)
                                startActivity(intent)
                                stopAudio()
                                finish()
                            }


                        } else {
                            AppPermissionDialog.showPermission(
                                this@IncomingCallActivity,
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

            Dexter.withContext(this@IncomingCallActivity)
                .withPermissions(Manifest.permission.RECORD_AUDIO)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {

                            if (isCallingFromApp) {
                                val intent =
                                    Intent(this@IncomingCallActivity, AppAudioCallingActivity::class.java)
                                intent.putExtra("remoteChannelId", remoteChannelId)
                                intent.putExtra("remoteUser", name)
                                intent.putExtra("callReceive", true)
                                intent.putExtra("profileImage", profileimage)
                                startActivity(intent)
                                stopAudio()
                                finish()
                            } else {

                                Toast.makeText(this@IncomingCallActivity, "", Toast.LENGTH_SHORT).show()
                                val intent =
                                    Intent(this@IncomingCallActivity, WebAudioCallingActivity::class.java)
                                intent.putExtra("remoteChannelId", remoteChannelId)
                                intent.putExtra("remoteUser", name)
                                intent.putExtra("callReceive", true)
                                intent.putExtra("profileImage", profileimage)
                                startActivity(intent)
                                stopAudio()
                                finish()
                            }


                        } else {
                            AppPermissionDialog.showPermission(
                                this@IncomingCallActivity,
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


}