package com.festum.festumfield.verstion.firstmodule.screens.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.festum.festumfield.Activity.ReelsActivity
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.FragmentFriendsListBinding
import com.festum.festumfield.databinding.UpcomingCallBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseFragment
import com.festum.festumfield.verstion.firstmodule.screens.adapters.FriendsListAdapter
import com.festum.festumfield.verstion.firstmodule.screens.adapters.GroupsListAdapter
import com.festum.festumfield.verstion.firstmodule.screens.main.chat.ChatActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.AppVideoCallingActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.webrtc.WebAudioCallingActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.ChatPinInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.GroupListItems
import com.festum.festumfield.verstion.firstmodule.utils.DeviceUtils
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*


@AndroidEntryPoint
class FriendsListFragment(private val chatPinInterface: ChatPinInterface?,private var fromId: String?) :
    BaseFragment<FriendsListViewModel>(), ChatPinInterface {

    private lateinit var binding: FragmentFriendsListBinding

    private var friendsListAdapter: FriendsListAdapter? = null
    private lateinit var upComingCallBinding: UpcomingCallBinding
    var dialog : Dialog? = null

    private var upComingCallUser: FriendsListItems? = null

    private var audioFileName: String = "skype"
    private var mMediaPlayer: MediaPlayer = MediaPlayer()

    companion object {

        var friendsListItems: ArrayList<FriendsListItems>? = null
        var groupsListItems: ArrayList<GroupListItems>? = null

    }

    override fun getContentView(): View {
        binding = FragmentFriendsListBinding.inflate(layoutInflater)
        return binding.root
    }


    /*override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        Toast.makeText(activity, args?.getString("text"), Toast.LENGTH_SHORT).show()
    }*/

    override fun initUi() {

        val bundle = this.arguments
        if (bundle != null) {
            val myInt = bundle.getString("text")
            Toast.makeText(requireActivity(), myInt, Toast.LENGTH_SHORT).show()
        }

        upComingCallBinding = UpcomingCallBinding.inflate(layoutInflater)

        binding.groupRecyclerview.visibility = View.GONE

        getMessage()

        val friendListBody = FriendListBody(search = "", limit = Int.MAX_VALUE, page = 1)
        viewModel.friendsList(friendListBody)

        val groupListItems = FriendListBody(search = "", limit = Int.MAX_VALUE, page = 1)
        viewModel.groupsList(groupListItems)


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
            binding.ivClearText.visibility = View.GONE
            binding.ivSearch.visibility = View.VISIBLE
            DeviceUtils.hideKeyboard(requireActivity())
        })

        binding.fbReels.setOnClickListener {
            startActivity(Intent(activity, ReelsActivity::class.java))
        }

    }

    override fun setObservers() {

        viewModel.friendsListData.observe(this) { friendsListData ->

            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {

                if (friendsListData != null) {

                    friendsListItems = friendsListData

                    /* Online - Offline*/
                    var onLine = false
                    val jsonObject = JSONObject(AppPreferencesDelegates.get().onLineUser)
                    val onlineUserChannelId = jsonObject.keys()
                    val onLineUserList = ArrayList<String>()

                    /* Pin - Unpin */
                    var setPin = false

                    friendsListData.forEach {

                        while (onlineUserChannelId.hasNext()) {
                            val key = onlineUserChannelId.next()
                            onLineUserList.add(key)
                        }

                        if (onLineUserList.contains(it.id?.uppercase())) {
                            it.online = true
                            onLine = true
                        }

                        if (it.isPinned == true) {
                            setPin = true
                        }

                    }

                    if (setPin) {
                        friendsListData.sortByDescending { item -> item.isPinned }
                    } else {
                        if (onLine){
                            friendsListData.sortByDescending { item -> item.online }
                        }else{
                            friendsListData.sortByDescending { item -> item.lastMessage?.updatedAt }
                        }
                    }

                    friendsListAdapter = FriendsListAdapter(requireActivity(), friendsListData, this, false)
                    binding.chatRecyclerview.adapter = friendsListAdapter

                    if (friendsListData.isEmpty()) {
                        binding.layoutEmpty.emptyLay.visibility = View.VISIBLE
                    } else {
                        binding.layoutEmpty.emptyLay.visibility = View.GONE
                    }

                    var friendsListItems = FriendsListItems()

                    if (!fromId.isNullOrEmpty()){

                        friendsListData.forEach {
                            if (it.id?.lowercase() == fromId?.lowercase()){
                                friendsListItems = it
                            }
                        }

                        val intent = Intent(requireActivity(), ChatActivity::class.java)
                        val jsonItem = Gson().toJson(friendsListItems)
                        intent.putExtra("friendsList", jsonItem)
                        requireActivity().startActivity(intent)
                        fromId = ""

                    }

                }

                binding.idPBLoading.visibility = View.GONE

            }

        }

        viewModel.groupsListData.observe(this) { groupListData ->

            groupsListItems = groupListData

        }

    }

    private fun filter(text: String) {

        val filteredList = ArrayList<FriendsListItems>()

        if (friendsListItems?.isNotEmpty() == true) {

            friendsListItems?.forEach { item ->
                if (item.fullName?.lowercase(Locale.getDefault())
                        ?.contains(text.lowercase(Locale.getDefault())) == true
                ) {
                    filteredList.add(item)
                }
            }

            if (filteredList.isEmpty()) {
                friendsListAdapter?.filterList(filteredList)
                Toast.makeText(context, "No User Found..", Toast.LENGTH_SHORT).show()
            } else {
                friendsListAdapter?.filterList(filteredList)
            }

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun getMessage() {

        SocketManager.mSocket?.on("userConnected") { args ->

            val data = args[0] as JSONObject

            AppPreferencesDelegates.get().onLineUser =
                data.optJSONObject("onlineUsers")?.toString() ?: ""

            activity?.runOnUiThread {

                val jsonObject = args[0] as JSONObject
                val mOnlineUsers = jsonObject.optJSONObject("onlineUsers")
                friendsListAdapter?.updateOnline(mOnlineUsers)

            }

            Log.e("TAG", "getUserStatus:$data")

        }?.on("offline") { args ->

            val data = args[0] as JSONObject
            Log.e("TAG", "offline:$data")

            AppPreferencesDelegates.get().onLineUser =
                data.optJSONObject("onlineUsers")?.toString() ?: ""

            activity?.runOnUiThread {
                val jsonObject = args[0] as JSONObject
                val mOnlineUsers = jsonObject.optString("userId")
                friendsListAdapter?.updateOffline(mOnlineUsers)
            }

        }?.on("endCall") { args ->

            try {
                val data = args[0] as JSONObject
                Log.e("TAG", "endCall: ${data}" )
            }catch (e : Exception){
                Log.e("TAG", "endCall: ${e.message}" )
            }

        }

//        val jsonObj = JSONObject()
//        jsonObj.put("channelID", AppPreferencesDelegates.get().channelId)
//        SocketManager.mSocket?.emit("init", jsonObj)?.on(
//            "onIncomingChat"
//        ) { args ->
//
//            val jsonObject = args[0] as JSONObject
//
//        }


        SocketManager.mSocket?.on(AppPreferencesDelegates.get().channelId) { args ->

            activity?.runOnUiThread {

                val message = args[0] as JSONObject

                val data = message.optJSONObject("data")

                when(message.optString("event").toString()){

                    "onIncomingChat" -> {


                        if (data != null) {

                            friendsListAdapter?.updateItem(data)
                            binding.chatRecyclerview.scrollToPosition(0)

                        }


                    }

                    "onGroupCallStarted" -> {  Log.e("TAG", "onGroupCallStarted---: $data")  }
                    "onCallStarted" -> {  Log.e("TAG", "onCallStarted---: $data")  }
                    "onGroupUpdate" -> {

                        activity?.runOnUiThread {

                            val friendListBody = FriendListBody(search = "", limit = Int.MAX_VALUE, page = 1)
                            viewModel.friendsList(friendListBody)
                            Log.e("TAG", "getMessage:--- $data")

                        }

                        Log.e("TAG", "onGroupUpdate---: $data")

                    }
                    "onGroupCreation" -> {

                        activity?.runOnUiThread {

                            val friendListBody = FriendListBody(search = "", limit = Int.MAX_VALUE, page = 1)
                            viewModel.friendsList(friendListBody)
                            Log.e("TAG", "getMessage:--- $data")

                        }

                        Log.e("TAG", "onGroupUpdate----: $data")

                    }

                }

                Log.e("TAG", "getMessage:--$message")

            }

        }

    }

    override fun checkItemPin(friendItem: FriendsListItems) {
        chatPinInterface?.setPin(friendItem)
    }

    override fun setPin(friendItem: FriendsListItems) {}


    fun setOnPin(itemData: FriendsListItems?, pinSet: Boolean) {

        friendsListAdapter?.updatePin(itemData, pinSet)

    }

    fun pinNotSelected(itemData: FriendsListItems?) {

        friendsListAdapter?.pinNotSelected(itemData)

    }

    private fun upComingCallView(
        upComingCallUser: FriendsListItems?,
        signal: String,
        name: String,
        isVideoCall: Boolean
    ) {

        Glide.with(requireActivity())
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

            if (isVideoCall){

                val intent = Intent(requireActivity(), AppVideoCallingActivity::class.java)
                intent.putExtra("remoteChannelId", signal.lowercase())
                intent.putExtra("remoteUser", name)
                intent.putExtra("callReceive", true)
                startActivity(intent)
                stopAudio()
                dialog?.dismiss()

            } else {

                val intent = Intent(requireActivity(), WebAudioCallingActivity::class.java)
                intent.putExtra("remoteChannelId", signal.lowercase())
                intent.putExtra("remoteUser", name)
                intent.putExtra("callReceive", true)
                startActivity(intent)
                stopAudio()
                dialog?.dismiss()

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

    private fun stopAudio() {

        try {

            mMediaPlayer.release()
            mMediaPlayer.pause()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

}