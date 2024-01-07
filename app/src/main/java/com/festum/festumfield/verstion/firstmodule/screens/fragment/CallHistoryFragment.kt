package com.festum.festumfield.verstion.firstmodule.screens.fragment

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ActivityVideoCallBinding
import com.festum.festumfield.databinding.FragmentCallHistoryBinding
import com.festum.festumfield.databinding.FragmentMapBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseFragment
import com.festum.festumfield.verstion.firstmodule.screens.adapters.CallHistoryAdapter
import com.festum.festumfield.verstion.firstmodule.screens.adapters.FriendsListAdapter
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.AppVideoCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.WebAudioCallingActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.CallHistoryInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.CallHistoryItem
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.CallHistoryResponse
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.GroupListItems
import com.festum.festumfield.verstion.firstmodule.utils.DeviceUtils
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.util.Locale

@AndroidEntryPoint
class CallHistoryFragment : BaseFragment<ChatViewModel>(), CallHistoryInterface {

    private lateinit var binding: FragmentCallHistoryBinding
    private var callHistoryAdapter : CallHistoryAdapter? = null
    private var callHistoryItemList : ArrayList<CallHistoryItem>? = null
    private lateinit var upComingCallBinding: ActivityVideoCallBinding
    private var upComingCallDialog: Dialog? = null

    private var isVideoCalling = false
    private var isAudioCalling = false
    private var isCallStart = false
    private var isCallAccpeted = false

    private var isCallUserId : String ?= null
    private var isCallUserName : String ?= null

    override fun getContentView(): View {
        binding = FragmentCallHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initUi() {

        viewModel.callHistory()

        getMessage()

        binding.edtSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.ivClearText.visibility = View.GONE
                    binding.ivSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearText.visibility = View.VISIBLE
                    binding.ivSearch.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
                val text = s.toString()
                filter(text)
                binding.ivSearch.visibility = View.GONE
                binding.ivClearText.visibility = View.VISIBLE
            }
        })

        binding.ivClearText.setOnClickListener(View.OnClickListener {
            binding.edtSearchText.setText("")
            binding.edtSearchText.clearFocus()
            binding.ivClearText.visibility = View.GONE
            binding.ivSearch.visibility = View.VISIBLE
            DeviceUtils.hideKeyboard(requireActivity())
        })

    }

    override fun setObservers() {

        viewModel.callHistoryData.observe(this) { callHistoryData ->

            if (lifecycle.currentState == Lifecycle.State.RESUMED) {

                callHistoryItemList = callHistoryData

                callHistoryData?.reverse()

                if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {

                    callHistoryAdapter = CallHistoryAdapter(requireActivity(), callHistoryData as ArrayList<CallHistoryItem?>,this)
                    binding.recycleCall.adapter = callHistoryAdapter

                    binding.idPBLoading.visibility = View.GONE
                }

                if (callHistoryData == null){
                    binding.idPBLoading.visibility = View.GONE
                }

            }

        }

    }

    private fun filter(text: String) {

        val filteredList = ArrayList<CallHistoryItem?>()

        if (callHistoryItemList?.isNotEmpty() == true) {

            callHistoryItemList?.forEach { item ->
                if (item.from?.fullName?.lowercase(Locale.getDefault())?.contains(text.lowercase(Locale.getDefault())) == true
                    || item.to?.fullName?.lowercase(Locale.getDefault())?.contains(text.lowercase(Locale.getDefault())) == true) {
                    filteredList.add(item)
                }
            }

            if (filteredList.isEmpty()) {
                callHistoryAdapter?.filterList(filteredList)
                Toast.makeText(context, "No User Found..", Toast.LENGTH_SHORT).show()
            } else {
                callHistoryAdapter?.filterList(filteredList)
            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCallFromHistory(item: CallHistoryItem?) {

        if (AppPreferencesDelegates.get().channelId.uppercase() == item?.from?.id?.uppercase()){

            if (item.isVideoCall == true){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onVideoCallPermission(true,item.to?.id.toString(),item.to?.fullName.toString(),item.to?.profileimage.toString())
                } else {
                    onVideoCallPermission(false,item.to?.id.toString(),item.to?.fullName.toString(),item.to?.profileimage.toString())
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onAudioCallPermission(true,item.to?.id.toString(),item.to?.fullName.toString(),item.to?.profileimage.toString())
                } else {
                    onAudioCallPermission(false,item.to?.id.toString(),item.to?.fullName.toString(),item.to?.profileimage.toString())
                }
            }

            isCallUserId = item.to?.id.toString()
            isCallUserName = item.to?.fullName.toString()

        } else {


            if (item?.isVideoCall == true){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onVideoCallPermission(true,item.from?.id.toString(),item.from?.fullName.toString(),item.from?.profileimage.toString())
                } else {
                    onVideoCallPermission(false,item.from?.id.toString(),item.from?.fullName.toString(),item.from?.profileimage.toString())
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onAudioCallPermission(true,item?.from?.id.toString(),item?.from?.fullName.toString(),item?.from?.profileimage.toString())
                } else {
                    onAudioCallPermission(false,item?.from?.id.toString(),item?.from?.fullName.toString(),item?.from?.profileimage.toString())
                }
            }

            isCallUserId = item?.from?.id.toString()
            isCallUserName = item?.from?.fullName.toString()


        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onVideoCallPermission(isTiramisu: Boolean,  remoteId : String, remoteName : String, remoteProfile :  String) {

        if (isTiramisu){
            Dexter.withContext(requireActivity())
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
                                jsonArray.put(remoteId.lowercase())
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

                            viewModel.callStart(from = AppPreferencesDelegates.get().channelId.lowercase(),to = remoteId.lowercase(),true,false,"")

                            upComingCallView(remoteId = remoteId, remoteName = remoteName, remoteProfile = remoteProfile)

                            isVideoCalling = true
                            isCallStart = true

                        } else {
                            AppPermissionDialog.showPermission(
                                requireActivity(),
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
            Dexter.withContext(requireActivity())
                .withPermissions(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {

                            val message = JSONObject().apply {

                                val jsonArray = JSONArray()
                                jsonArray.put(remoteId.lowercase())
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

                            viewModel.callStart(from = AppPreferencesDelegates.get().channelId.lowercase(),to = remoteId.lowercase(),true,false,"")

                            upComingCallView(remoteId = remoteId, remoteName = remoteName, remoteProfile = remoteProfile)

                            isVideoCalling = true
                            isCallStart = true

                        } else {
                            AppPermissionDialog.showPermission(
                                requireActivity(),
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
    private fun onAudioCallPermission(isTiramisu: Boolean , remoteId : String, remoteName : String, remoteProfile :  String) {

        if (isTiramisu){

            Dexter.withContext(requireActivity())
                .withPermissions(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {

                            val message = JSONObject().apply {

                                val jsonArray = JSONArray()
                                jsonArray.put(remoteId.lowercase())
                                jsonArray.put(AppPreferencesDelegates.get().channelId.lowercase())
                                put("memberIds", jsonArray)
                                put("fromId", AppPreferencesDelegates.get().channelId.lowercase())
                                put("name", AppPreferencesDelegates.get().userName)
                                put("isVideoCall", false)
                                put("isCallingFromApp", true)
                                put("isGroupCalling", false)

                            }

                            SocketManager.mSocket?.emit("callUser", message)

                            upComingCallView(remoteId = remoteId, remoteName = remoteName, remoteProfile = remoteProfile)

                            /* Call Start */
                            viewModel.callStart(from = AppPreferencesDelegates.get().channelId.lowercase(),to = remoteId.lowercase(),false,false,"")

                            isAudioCalling = true
                            isCallStart = true

                        } else {
                            AppPermissionDialog.showPermission(
                                requireActivity(),
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

            Dexter.withContext(requireActivity())
                .withPermissions(Manifest.permission.RECORD_AUDIO)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                        if (permission?.areAllPermissionsGranted() == true) {

                            val message = JSONObject().apply {

                                val jsonArray = JSONArray()
                                jsonArray.put(remoteId.lowercase())
                                jsonArray.put(AppPreferencesDelegates.get().channelId.lowercase())
                                put("memberIds", jsonArray)
                                put("fromId", AppPreferencesDelegates.get().channelId.lowercase())
                                put("name", AppPreferencesDelegates.get().userName)
                                put("isVideoCall", false)
                                put("isCallingFromApp", true)
                                put("isGroupCalling", false)

                            }

                            SocketManager.mSocket?.emit("callUser", message)

                            upComingCallView(remoteId = remoteId, remoteName = remoteName, remoteProfile = remoteProfile)

                            /* Call Start */
                            viewModel.callStart(from = AppPreferencesDelegates.get().channelId.lowercase(),to =remoteId.lowercase(),false,false,"")

                            isAudioCalling = true
                            isCallStart = true

                        } else {
                            AppPermissionDialog.showPermission(
                                requireActivity(),
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
        remoteId : String, remoteName : String, remoteProfile :  String,
    ) {

        upComingCallBinding = ActivityVideoCallBinding.inflate(layoutInflater)

        upComingCallDialog = Dialog(requireActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        upComingCallDialog?.setContentView(upComingCallBinding.root)

        Glide.with(requireActivity())
            .load(Constans.Display_Image_URL + remoteProfile)
            .placeholder(R.drawable.ic_user_img).into(upComingCallBinding.videocallImage)

        upComingCallBinding.llVideoCall.visibility = View.GONE
        upComingCallBinding.llMute.visibility = View.GONE

        upComingCallBinding.videocallUsername.text = remoteName

        upComingCallBinding.llCallCut.setOnClickListener {

            val jsonObj = JSONObject()
            jsonObj.put("id", remoteId)
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
                val i = Intent(requireActivity(), AppVideoCallingActivity::class.java)
                i.putExtra("remoteChannelId", isCallUserId?.lowercase())
                i.putExtra("remoteUser", isCallUserName)
                i.putExtra("callId", AppPreferencesDelegates.get().isCallId)
                startActivityForResult(i, IntentUtil.IS_VIDEO_CALLING)
                isVideoCalling = false
                isCallAccpeted = true

                Handler(Looper.getMainLooper()).postDelayed({ upComingCallDialog?.dismiss() }, 1000)

            }

            if (isAudioCalling) {

                val i = Intent(requireActivity(), WebAudioCallingActivity::class.java)
                i.putExtra("remoteChannelId", isCallUserId?.lowercase())
                i.putExtra("remoteUser", isCallUserName)
                i.putExtra("callId", AppPreferencesDelegates.get().isCallId)
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
                viewModel.callEnd(AppPreferencesDelegates.get().isCallId)

            } catch (e: Exception) {
                upComingCallDialog?.dismiss()
            }

        }

    }

}