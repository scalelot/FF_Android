package com.festum.festumfield.verstion.firstmodule.screens.main

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.festum.festumfield.Activity.*
import com.festum.festumfield.Fragment.ContactFragment
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ActivityHomeBinding
import com.festum.festumfield.databinding.UpcomingCallBinding
import com.festum.festumfield.verstion.firstmodule.FestumApplicationClass
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.screens.fragment.CallHistoryFragment
import com.festum.festumfield.verstion.firstmodule.screens.fragment.FriendsListFragment
import com.festum.festumfield.verstion.firstmodule.screens.fragment.FriendsListFragment.Companion.friendsListItems
import com.festum.festumfield.verstion.firstmodule.screens.fragment.MapFragment
import com.festum.festumfield.verstion.firstmodule.screens.main.chat.ChatActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.group.NewGroupActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.profile.ProfilePreviewActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.AppAudioCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.AppGroupVideoCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.AppVideoCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.WebAudioCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.WebVideoCallingActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates.Companion.get
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.ChatPinInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import io.socket.emitter.Emitter
import org.json.JSONObject


@AndroidEntryPoint
class HomeActivity : BaseActivity<ProfileViewModel>(), ChatPinInterface {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var upComingCallBinding: UpcomingCallBinding
    private var friendsFragment: FriendsListFragment? = null
    private var itemData: FriendsListItems? = null
    private var upComingCallUser: FriendsListItems? = null
    var dialog: Dialog? = null

    var callId: String? = null
    var messageId: String? = null
    var fromId:String? = null
    var toId:String? = null
    var toUserName:String? = null
    var banner:String? = null

    private var audioFileName: String = "skype"
    private var mMediaPlayer: MediaPlayer = MediaPlayer()


    override fun getContentView(): View {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is FriendsListFragment) {
            friendsFragment = fragment
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun setupUi() {


//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            messageId = extras.getString("messageid", "");
        val intent = intent
        val extras = intent.extras


         banner = if (extras != null) extras.getString("banner", "") else ""
        callId = if (extras != null) extras.getString("callId", "") else ""
         messageId = if (extras != null) extras.getString("messageId", "") else ""
         fromId = if (extras != null) extras.getString("fromId", "") else ""
         toId = if (extras != null) extras.getString("toId", "") else ""
        toUserName = if (extras != null) extras.getString("toUserName", "") else ""


        Handler(Looper.getMainLooper()).postDelayed({

            if (callId != null && callId?.isNotEmpty() == true){

                friendsListItems?.forEach {

                    if (it.id?.contains(fromId.toString().lowercase()) == true) {
                        upComingCallUser = it
                    }

                }

                upComingCallView(
                    upComingCallUser,
                    fromId.toString().lowercase(),
                    toUserName.toString(),
                    true,
                    true,
                    false,
                    banner
                )

            }


        }, 800)

        Log.e("TAG", "banner:------ $banner")
        Log.e("TAG", "callId:------ $callId")
        Log.e("TAG", "fromId:------ $fromId")
        Log.e("TAG", "toId:------ $toId")
        Log.e("TAG", "fromUserName:------ $toUserName")
        Log.e("TAG", "messageId:------ $messageId")

        upComingCallBinding = UpcomingCallBinding.inflate(layoutInflater)

        Handler(Looper.getMainLooper()).postDelayed({

            getUpComingCall()

        }, 500)

        dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog?.setContentView(upComingCallBinding.root)

        binding.userImg.setOnClickListener {

            if (AppPreferencesDelegates.get().userName.isBlank()) {
                /* For create profile */
                createProfileDialog()
            } else {
                /* For Start Profile */
                startActivity(Intent(this@HomeActivity, ProfilePreviewActivity::class.java))
            }
        }

        binding.userName.setOnClickListener {

            if (AppPreferencesDelegates.get().userName.isBlank()) {
                /* For create profile */
                createProfileDialog()
            } else {
                /* For Start Profile */
                startActivity(Intent(this@HomeActivity, ProfilePreviewActivity::class.java))
            }

        }

        binding.hi.setOnClickListener {

            if (AppPreferencesDelegates.get().userName.isBlank()) {
                /* For create profile */
                createProfileDialog()
            } else {
                /* For Start Profile */
                startActivity(Intent(this@HomeActivity, ProfilePreviewActivity::class.java))
            }

        }



        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            selectFragment(item)
            true
        }

        binding.ivLikes.setOnClickListener {
            startActivity(Intent(this@HomeActivity, LikeAndCommentActivity::class.java))
        }

        binding.ivPromotion.setOnClickListener {
            startActivity(Intent(this@HomeActivity, PromotionActivity::class.java))
        }

        binding.ivBusinessAccount.setOnClickListener {
            startActivity(Intent(this@HomeActivity, DisplayAllProductActivity::class.java))
        }

        binding.popupBtn.setOnClickListener(View.OnClickListener {
            val popup = PopupMenu(this@HomeActivity, binding.popupBtn)
            popup.menuInflater.inflate(R.menu.main_menu, popup.menu)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.requests -> {
                        startActivity(Intent(this@HomeActivity, RequestActivity::class.java))
                        return@OnMenuItemClickListener true
                    }

                    R.id.setting -> {

                        this.startActivityForResult(
                            Intent(
                                this@HomeActivity,
                                SettingActivity::class.java
                            ), 1
                        )
                        return@OnMenuItemClickListener true
                    }

                    R.id.new_broadcast -> {
                        startActivity(Intent(this@HomeActivity, CreateNewGroupActivity::class.java))
                        return@OnMenuItemClickListener true
                    }

                    R.id.new_group -> {
                        startActivity(Intent(this@HomeActivity, NewGroupActivity::class.java))
                        return@OnMenuItemClickListener true
                    }

                    else -> true
                }
            })
            popup.show()
        })

        binding.pinBack.setOnClickListener {
            pinVisibility(isPinClick = true, isPinSet = false)
            friendsFragment?.pinNotSelected(itemData)
        }

        binding.pin.setOnClickListener {
            viewModel.setPin(itemData, true)
        }

        binding.unPin.setOnClickListener {
            viewModel.setPin(itemData, false)
        }

    }

    @SuppressLint("SetTextI18n")
    override fun setupObservers() {

        viewModel.profileData.observe(this) { profileData ->


            if (profileData != null) {

                get().channelId = profileData.channelID.toString()
                get().businessProfile = profileData.isBusinessProfileCreated == true
                get().userName = profileData.fullName.toString()
                get().personalProfile = true


                val applicationClass: FestumApplicationClass = application as FestumApplicationClass
                applicationClass.onCreate()

                val menu: Menu = binding.bottomNavigationView.menu
                selectFragment(menu.getItem(0))

                if (profileData.fullName.isNullOrEmpty()) {
                    binding.userName.text = "+" + profileData.contactNo
                } else {
                    binding.userName.text = profileData.fullName

                }

                Glide.with(this@HomeActivity)
                    .load(Constans.Display_Image_URL + profileData.profileimage)
                    .placeholder(R.drawable.ic_user).into(binding.userImg)

            }

        }

        viewModel.setPinData.observe(this) {

            if (it != null) {
                Snackbar.make(binding.mainView, it.message.toString(), Snackbar.LENGTH_SHORT).show()
            }

            itemData = if (it?.status == 200 && it.message?.contains("Pinned") == true) {
                pinVisibility(isPinClick = true, isPinSet = true)
                friendsFragment?.setOnPin(itemData, true)
                null
            } else {
                pinVisibility(isPinClick = true, isPinSet = false)
                friendsFragment?.setOnPin(itemData, false)
                null
            }

        }

    }

    override fun onResume() {
        super.onResume()

        /* Get Profile */
        viewModel.getProfile()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {

            val resultString = data?.getBooleanExtra("darkTheme", false)

            if (resultString == true) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            /* For Dark Theme */
            val intent = intent
            overridePendingTransition(0, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)

        }

    }

    private fun selectFragment(item: MenuItem) {
        item.isChecked = true
        when (item.itemId) {
            R.id.chat ->

                if (fromId != null && fromId?.isNotEmpty() == true && toId != null && toId?.isNotEmpty() == true && callId.isNullOrEmpty() && callId == null){
                    Handler(Looper.getMainLooper()).postDelayed({

                        pushFragment(FriendsListFragment(this,fromId))
                        fromId = ""
                        toId = ""

                    }, 500)
                }else{
                    pushFragment(FriendsListFragment(this,""))
                }

            R.id.location ->
                if (AppPreferencesDelegates.get().userName.isBlank()) {
                    /* For create profile */
                    createProfileDialog()
                } else {
                    onLocationCheck()
                }

            R.id.call ->
                pushFragment(CallHistoryFragment())

            R.id.contact ->
                pushFragment(ContactFragment())
        }
    }

    private fun pushFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.replace(R.id.frameLayout, fragment)
        ft.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun onPermission() {

        Dexter.withContext(this@HomeActivity)
            .withPermissions(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                    if (permission?.areAllPermissionsGranted() == true) {
                        pushFragment(MapFragment())
                    } else {
                        AppPermissionDialog.showPermission(
                            this@HomeActivity,
                            title = getString(R.string.location_permission_title),
                            message = getString(R.string.location_permission)
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

    private fun createProfileDialog() {

        var profileDialogView: View

        try {

            profileDialogView = LayoutInflater.from(this@HomeActivity)
                .inflate(R.layout.create_profile_dialog, null, false)
            val allowed = profileDialogView.findViewById<AppCompatButton>(R.id.dialog_continue)
            val cancel = profileDialogView.findViewById<AppCompatButton>(R.id.dialog_skip)
            val close = profileDialogView.findViewById<ImageView>(R.id.dialog_close)

            val dialog = MaterialAlertDialogBuilder(this@HomeActivity)
                .setCancelable(false)
                .setView(profileDialogView)
                .show()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            allowed.setOnClickListener {
                startActivity(Intent(this@HomeActivity, ProfilePreviewActivity::class.java))
                dialog.dismiss()
            }
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            close.setOnClickListener {
                dialog.dismiss()
            }

        } catch (e: Exception) {
            Log.e("NoServerDialog", e.message.toString())
        }

    }

    override fun setPin(friendItem: FriendsListItems) {
        pinVisibility(false, friendItem.isPinned)
        itemData = friendItem
    }

    override fun checkItemPin(friendItem: FriendsListItems) {}

    private fun pinVisibility(isPinClick: Boolean, isPinSet: Boolean?) {

        if (!isPinClick) {
            binding.userImg.visibility = View.INVISIBLE
            binding.pinBack.visibility = View.VISIBLE
            binding.hi.visibility = View.VISIBLE
            binding.userName.visibility = View.VISIBLE
            binding.ivLikes.visibility = View.GONE
            binding.ivPromotion.visibility = View.GONE
            binding.ivBusinessAccount.visibility = View.GONE
            binding.pin.visibility = View.VISIBLE

            if (isPinSet == true) {
                binding.unPin.visibility = View.VISIBLE
            } else {
                binding.unPin.visibility = View.GONE
            }

        } else {
            binding.userImg.visibility = View.VISIBLE
            binding.pinBack.visibility = View.GONE
            binding.hi.visibility = View.VISIBLE
            binding.userName.visibility = View.VISIBLE
            binding.ivLikes.visibility = View.VISIBLE
            binding.ivPromotion.visibility = View.VISIBLE
            binding.ivBusinessAccount.visibility = View.VISIBLE
            binding.pin.visibility = View.GONE
            binding.unPin.visibility = View.GONE
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getUpComingCall() {

        SocketManager.mSocket?.on("incomingCall") { args ->

            val data = args[0] as JSONObject

            runOnUiThread {

                val signal = data.optJSONObject("signal")
                val type = signal?.optString("type")
                val sdp = signal?.optString("sdp")
                val from = data.optString("fromId")
                val name = data.optString("name")
//                val isVideoCall = data.optBoolean("channelID")
                val isVideoCall = data.optBoolean("isVideoCall")
                val isCallingFromApp = data.optBoolean("isCallingFromApp")
                val isGroupCalling = data.optBoolean("isGroupCalling")


                friendsListItems?.forEach {

                    if (it.id?.contains(from.toString().lowercase()) == true) {
                        upComingCallUser = it
                    }

                }

                playAudio(this@HomeActivity, "skype")


                runOnUiThread {

                    Handler(Looper.getMainLooper()).postDelayed({

                        if (isGroupCalling) {

                            upComingGroupCallView(
                                upComingCallUser,
                                from.toString().lowercase(),
                                name,
                                isVideoCall,
                                isCallingFromApp,
                                true
                            )

                        } else {
                            upComingCallView(
                                upComingCallUser,
                                from.toString().lowercase(),
                                name,
                                isVideoCall,
                                isCallingFromApp,
                                false,
                                ""
                            )
                        }


                    }, 500)

                }

            }

        }?.on("callUser") { args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "callUser:---- $data")

        }?.on("endCall") { args ->

                try {

                    val data = args[0] as JSONObject

                    Log.e("TAG", "callUser:---- $data")



                    Handler(Looper.getMainLooper()).postDelayed({

                        stopAudio()
                        dialog?.dismiss()

                    }, 500)

                    /* Call End */
                    /*viewModel.callEnd(callId)*/

                } catch (e: Exception) {

                    stopAudio()

                    dialog?.dismiss()

                }

        }

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun upComingCallView(
        upComingCallUser: FriendsListItems?,
        signal: String,
        name: String,
        isVideoCall: Boolean,
        isCallingFromApp: Boolean,
        isGroupCalling: Boolean,
        banner: String?
    ) {

        if (banner != null && !banner.isNullOrEmpty()){
            Glide.with(this@HomeActivity)
                .load(Constans.Display_Image_URL + banner)
                .placeholder(R.drawable.ic_user_img).into(upComingCallBinding.upcomingcallUserImg)
        }else{
            Glide.with(this@HomeActivity)
                .load(Constans.Display_Image_URL + upComingCallUser?.profileimage)
                .placeholder(R.drawable.ic_user_img).into(upComingCallBinding.upcomingcallUserImg)
        }

        upComingCallBinding.upcomingUsername.text = name

        upComingCallBinding.llCallCut.setOnClickListener {

            val jsonObj = JSONObject()
            jsonObj.put("id", signal)
            SocketManager.mSocket?.emit("endCall", jsonObj)
            stopAudio()
            dialog?.dismiss()

        }

        upComingCallBinding.llCallRecive.setOnClickListener {

            if (isVideoCall) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onCameraPermission(
                        isCallingFromApp,
                        signal.lowercase(),
                        name,
                        true,
                        isGroupCalling,
                        upComingCallUser
                    )
                } else {

                    onCameraPermission(
                        isCallingFromApp,
                        signal.lowercase(),
                        name,
                        false,
                        isGroupCalling,
                        upComingCallUser
                    )
                }

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onAudioPermission(
                        isCallingFromApp,
                        signal.lowercase(),
                        name,
                        upComingCallUser?.profileimage,
                        true
                    )
                } else {
                    onAudioPermission(
                        isCallingFromApp,
                        signal.lowercase(),
                        name,
                        upComingCallUser?.profileimage,
                        false
                    )
                }

            }

        }

        dialog?.show()

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun upComingGroupCallView(
        upComingCallUser: FriendsListItems?,
        signal: String,
        name: String,
        isVideoCall: Boolean,
        isCallingFromApp: Boolean,
        isGroupCalling: Boolean
    ) {

        Glide.with(this@HomeActivity)
            .load(Constans.Display_Image_URL + upComingCallUser?.profileimage)
            .placeholder(R.drawable.ic_user_img).into(upComingCallBinding.upcomingcallUserImg)

        upComingCallBinding.upcomingUsername.text = name

        upComingCallBinding.llCallCut.setOnClickListener {

            val jsonObj = JSONObject()
            jsonObj.put("id", signal)
            SocketManager.mSocket?.emit("endCall", jsonObj)
            stopAudio()
            dialog?.dismiss()

        }

        upComingCallBinding.llCallRecive.setOnClickListener {

            if (isVideoCall) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onCameraPermission(
                        isCallingFromApp,
                        signal.lowercase(),
                        name,
                        true,
                        isGroupCalling,
                        upComingCallUser
                    )
                } else {
                    onCameraPermission(
                        isCallingFromApp,
                        signal.lowercase(),
                        name,
                        false,
                        isGroupCalling,
                        upComingCallUser
                    )
                }

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onAudioPermission(
                        isCallingFromApp,
                        signal.lowercase(),
                        name,
                        upComingCallUser?.profileimage,
                        true
                    )
                } else {
                    onAudioPermission(
                        isCallingFromApp,
                        signal.lowercase(),
                        name,
                        upComingCallUser?.profileimage,
                        false
                    )
                }

            }

        }

        dialog?.show()

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

    private fun setupSocketListeners() {

        SocketManager.mSocket?.on(get().channelId, onIncomingChatListener)

    }

    private val onIncomingChatListener = Emitter.Listener { args ->

        val message = args[0] as JSONObject

        Log.e("TAG", "getMessage: -----$message")

        val data = message.optJSONObject("data")

        when (message.optString("event").toString()) {

            "onIncomingChat" -> {
                Log.e("TAG", "onIncomingChat---: $data")
            }

            "onGroupCallStarted" -> {
                Log.e("TAG", "onGroupCallStarted---: $data")
            }

            "onCallStarted" -> {

                val callId = data?.optString("callid")

                get().isCallId = callId ?: ""

            }

            "onGroupUpdate" -> {
                Log.e("TAG", "onGroupUpdate---: $data")
            }

            "onGroupCreation" -> {
                Log.e("TAG", "onGroupCreation---: $data")
            }

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

        ) {

        if (isTiramisu) {
            Dexter.withContext(this@HomeActivity)
                .withPermissions(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {


                            if (isCallingFromApp) {

                                if (isGroupCalling){

                                    val intent = Intent(this@HomeActivity, AppGroupVideoCallingActivity::class.java)
                                    val jsonItem = Gson().toJson(upComingCallUser)
                                    intent.putExtra("groupList", jsonItem)
                                    startActivity(intent)
                                    stopAudio()
                                    dialog?.dismiss()

                                }else{

                                    val intent =
                                        Intent(this@HomeActivity, AppVideoCallingActivity::class.java)
                                    intent.putExtra("remoteChannelId", remoteChannelId)
                                    intent.putExtra("remoteUser", name)
                                    intent.putExtra("callReceive", true)
                                    startActivity(intent)
                                    stopAudio()
                                    dialog?.dismiss()

                                }


                            } else {

                                val intent =
                                    Intent(this@HomeActivity, WebVideoCallingActivity::class.java)
                                intent.putExtra("remoteChannelId", remoteChannelId)
                                intent.putExtra("remoteUser", name)
                                intent.putExtra("callReceive", true)
                                startActivity(intent)
                                stopAudio()
                                dialog?.dismiss()

                            }

                        } else {
                            AppPermissionDialog.showPermission(
                                this@HomeActivity,
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
            Dexter.withContext(this@HomeActivity)
                .withPermissions(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {

                            if (isCallingFromApp && isGroupCalling) {

                                val intent = Intent(this@HomeActivity, AppGroupVideoCallingActivity::class.java)
                                val jsonItem = Gson().toJson(upComingCallUser)
                                intent.putExtra("groupList", jsonItem)
                                startActivity(intent)
                                stopAudio()
                                dialog?.dismiss()
                            } else {
                                if (isCallingFromApp) {
                                    val intent =
                                        Intent(this@HomeActivity, AppVideoCallingActivity::class.java)
                                    intent.putExtra("remoteChannelId", remoteChannelId)
                                    intent.putExtra("remoteUser", name)
                                    intent.putExtra("callReceive", true)
                                    startActivity(intent)
                                    stopAudio()
                                    dialog?.dismiss()
                                } else {

                                    val intent =
                                        Intent(this@HomeActivity, WebVideoCallingActivity::class.java)
                                    intent.putExtra("remoteChannelId", remoteChannelId)
                                    intent.putExtra("remoteUser", name)
                                    intent.putExtra("callReceive", true)
                                    startActivity(intent)
                                    stopAudio()
                                    dialog?.dismiss()
                                }

                            }

                        } else {
                            AppPermissionDialog.showPermission(
                                this@HomeActivity,
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

            Dexter.withContext(this@HomeActivity)
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
                                    Intent(this@HomeActivity, AppAudioCallingActivity::class.java)
                                intent.putExtra("remoteChannelId", remoteChannelId)
                                intent.putExtra("remoteUser", name)
                                intent.putExtra("callReceive", true)
                                intent.putExtra("profileImage", profileimage)
                                startActivity(intent)
                                stopAudio()
                                dialog?.dismiss()
                            } else {
                                val intent =
                                    Intent(this@HomeActivity, WebAudioCallingActivity::class.java)
                                intent.putExtra("remoteChannelId", remoteChannelId)
                                intent.putExtra("remoteUser", name)
                                intent.putExtra("callReceive", true)
                                intent.putExtra("profileImage", profileimage)
                                startActivity(intent)
                                stopAudio()
                                dialog?.dismiss()
                            }


                        } else {
                            AppPermissionDialog.showPermission(
                                this@HomeActivity,
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

            Dexter.withContext(this@HomeActivity)
                .withPermissions(Manifest.permission.RECORD_AUDIO)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {

                            if (isCallingFromApp) {
                                val intent =
                                    Intent(this@HomeActivity, AppAudioCallingActivity::class.java)
                                intent.putExtra("remoteChannelId", remoteChannelId)
                                intent.putExtra("remoteUser", name)
                                intent.putExtra("callReceive", true)
                                intent.putExtra("profileImage", profileimage)
                                startActivity(intent)
                                stopAudio()
                                dialog?.dismiss()
                            } else {

                                Toast.makeText(this@HomeActivity, "", Toast.LENGTH_SHORT).show()
                                val intent =
                                    Intent(this@HomeActivity, WebAudioCallingActivity::class.java)
                                intent.putExtra("remoteChannelId", remoteChannelId)
                                intent.putExtra("remoteUser", name)
                                intent.putExtra("callReceive", true)
                                intent.putExtra("profileImage", profileimage)
                                startActivity(intent)
                                stopAudio()
                                dialog?.dismiss()
                            }


                        } else {
                            AppPermissionDialog.showPermission(
                                this@HomeActivity,
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

    private fun isDarkModeOn(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

    private fun changeToNightMode() {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

    }

    private fun onLocationCheck() {

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
            onPermission()
        }

    }


}