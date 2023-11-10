package com.festum.festumfield.verstion.firstmodule.screens.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.festum.festumfield.Activity.*
import com.festum.festumfield.Fragment.CallsFragment
import com.festum.festumfield.Fragment.ContactFragment
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ActivityHomeBinding
import com.festum.festumfield.databinding.UpcomingCallBinding
import com.festum.festumfield.verstion.firstmodule.FestumApplicationClass
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.screens.fragment.FriendsListFragment
import com.festum.festumfield.verstion.firstmodule.screens.fragment.FriendsListFragment.Companion.friendsListItems
import com.festum.festumfield.verstion.firstmodule.screens.fragment.MapFragment
import com.festum.festumfield.verstion.firstmodule.screens.main.group.NewGroupActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.profile.ProfilePreviewActivity
import com.festum.festumfield.verstion.firstmodule.screens.webrtc.AppCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.webrtc.AudioCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.webrtc.VideoCallingActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.ChatPinInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class HomeActivity : BaseActivity<ProfileViewModel>(), ChatPinInterface {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var upComingCallBinding: UpcomingCallBinding
    private var friendsFragment: FriendsListFragment? = null
    private var itemData: FriendsListItems? = null
    private var upComingCallUser: FriendsListItems? = null
    var sdpAnswer = ""

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

    override fun setupUi() {

        getUpComingCall()

        /*PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions()
        )*/

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
                        startActivity(Intent(this@HomeActivity, SettingActivity::class.java))
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

                AppPreferencesDelegates.get().channelId = profileData.channelID.toString()
                AppPreferencesDelegates.get().businessProfile =
                    profileData.isBusinessProfileCreated == true
                AppPreferencesDelegates.get().userName = profileData.fullName.toString()

                AppPreferencesDelegates.get().personalProfile = true

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

    private fun selectFragment(item: MenuItem) {
        item.isChecked = true
        when (item.itemId) {
            R.id.chat ->
                pushFragment(FriendsListFragment(this))

            R.id.location ->
                if (AppPreferencesDelegates.get().userName.isBlank()) {
                    /* For create profile */
                    createProfileDialog()
                } else {
                    onPermission()
                }

            R.id.call ->
                pushFragment(CallsFragment())

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

    private fun getUpComingCall() {

        SocketManager.mSocket?.on("incomingCall") { args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "getUpComingCall: $data")

            runOnUiThread {

                val signal = data.optJSONObject("signal")
                val type = signal?.optString("type")
                val sdp = signal?.optString("sdp")
                val from = data.optString("fromId")
                val name = data.optString("name")
//                val isVideoCall = data.optBoolean("channelID")
                val isVideoCall = data.optBoolean("isVideoCall")


                friendsListItems?.forEach {

                    if (it.id?.contains(from.toString().lowercase()) == true) {
                        upComingCallUser = it
                    }

                }

                playAudio(this@HomeActivity, "skype")

                Handler(Looper.getMainLooper()).postDelayed({

                     upComingCallView(upComingCallUser, from.toString().lowercase(), name, isVideoCall)

                }, 300)

            }

        }?.on("callUser"){ args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "callUser:---- $data")

        }

    }


    private fun upComingCallView(
        upComingCallUser: FriendsListItems?,
        signal: String,
        name: String,
        isVideoCall: Boolean
    ) {

        upComingCallBinding = UpcomingCallBinding.inflate(layoutInflater)

        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(upComingCallBinding.root)

        Glide.with(this@HomeActivity)
            .load(Constans.Display_Image_URL + upComingCallUser?.profileimage)
            .placeholder(R.drawable.ic_user_img).into(upComingCallBinding.upcomingcallUserImg)

        upComingCallBinding.upcomingUsername.text = name

        upComingCallBinding.llCallCut.setOnClickListener {

            val jsonObj = JSONObject()
            jsonObj.put("id", signal)
            SocketManager.mSocket?.emit("endCall", jsonObj)
            stopAudio()
            dialog.dismiss()

        }

        upComingCallBinding.llCallRecive.setOnClickListener {

            /*val webRtcMessage = JSONObject().apply {

                put("displayName",AppPreferencesDelegates.get().userName)
                put("uuid",AppPreferencesDelegates.get().channelId)
                put("dest",signal.lowercase())
                put("channelID",signal.lowercase())

            }

            SocketManager.mSocket?.emit("webrtcMessage",webRtcMessage)*/

            if (isVideoCall){

                val intent = Intent(this@HomeActivity, AppCallingActivity::class.java)
                intent.putExtra("remoteChannelId", signal.lowercase())
                intent.putExtra("remoteUser", name)
                intent.putExtra("callReceive", true)
                startActivity(intent)
                stopAudio()
                dialog.dismiss()

            } else {

                val intent = Intent(this@HomeActivity, AudioCallingActivity::class.java)
                intent.putExtra("remoteChannelId", signal.lowercase())
                intent.putExtra("remoteUser", name)
                intent.putExtra("callReceive", true)
                startActivity(intent)
                stopAudio()
                dialog.dismiss()

            }



        }

        dialog.show()

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

}