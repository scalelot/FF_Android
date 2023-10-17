package com.festum.festumfield.verstion.firstmodule.screens.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.festum.festumfield.Activity.ReelsActivity
import com.festum.festumfield.databinding.FragmentFriendsListBinding
import com.festum.festumfield.verstion.firstmodule.FestumApplicationClass
import com.festum.festumfield.verstion.firstmodule.screens.BaseFragment
import com.festum.festumfield.verstion.firstmodule.screens.adapters.FriendsListAdapter
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.ChatPinInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.utils.DeviceUtils
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import org.json.JSONObject
import java.util.*


@AndroidEntryPoint
class FriendsListFragment(private val chatPinInterface: ChatPinInterface?) :
    BaseFragment<FriendsListViewModel>(), ChatPinInterface {

    private lateinit var binding: FragmentFriendsListBinding

    private var friendsListAdapter: FriendsListAdapter? = null

    companion object {

        var friendsListItems: ArrayList<FriendsListItems>? = null

    }

    override fun getContentView(): View {
        binding = FragmentFriendsListBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        Toast.makeText(activity, args?.getString("text"), Toast.LENGTH_SHORT).show()
    }

    override fun initUi() {

        val bundle = this.arguments
        if (bundle != null) {
            val myInt = bundle.getString("text")
            Toast.makeText(requireActivity(), myInt, Toast.LENGTH_SHORT).show()
        }

        getMessage()

        val friendListBody = FriendListBody(search = "", limit = Int.MAX_VALUE, page = 1)
        viewModel.friendsList(friendListBody)

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

            Log.e("TAG", "setObservers:--- $friendsListData")

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

                    friendsListAdapter =
                        FriendsListAdapter(requireActivity(), friendsListData, this, false)
                    binding.chatRecyclerview.adapter = friendsListAdapter

                    if (friendsListData.isEmpty()) {
                        binding.layoutEmpty.emptyLay.visibility = View.VISIBLE
                    } else {
                        binding.layoutEmpty.emptyLay.visibility = View.GONE
                    }

                }

                binding.idPBLoading.visibility = View.GONE

            }

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

        SocketManager.mSocket?.on("newMessage") { args ->

            val data = args[0] as JSONObject

            activity?.runOnUiThread {

                friendsListAdapter?.updateItem(data)
                binding.chatRecyclerview.scrollToPosition(0)

            }

        }?.on("userConnected") { args ->

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

        }?.on("updateUserMedia") { args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "updateMyMedia: $data")

        }?.on("answerCall") { args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "answerCall: $data")

        }?.on("endCall") { args ->

            try {
                val data = args[0] as JSONObject
                Log.e("TAG", "endCall: ${data}" )
            }catch (e : Exception){
                Log.e("TAG", "endCall: ${e.message}" )
            }

        }?.on("webrtcUpdateUserMedia"){  args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "endCall: $data")

        }?.on("webrtcMessage"){  args ->

            val data = args[0] as JSONObject

//            Log.e("TAG", "webrtcMessage: $data")

        }?.on("incomingCall"){  args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "incomingCall: $data")

        }?.on("incomingNotification"){  args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "incomingNotification: $data")

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


}