package com.festum.festumfield.verstion.firstmodule.screens.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.festum.festumfield.Activity.ReelsActivity
import com.festum.festumfield.MyApplication
import com.festum.festumfield.databinding.FragmentFriendsListBinding
import com.festum.festumfield.verstion.firstmodule.FestumApplicationClass
import com.festum.festumfield.verstion.firstmodule.screens.BaseFragment
import com.festum.festumfield.verstion.firstmodule.screens.adapters.FriendsListAdapter
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.utils.DeviceUtils
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import org.json.JSONObject
import java.util.*


@AndroidEntryPoint
class FriendsListFragment : BaseFragment<FriendsListViewModel>() {

    private lateinit var binding: FragmentFriendsListBinding

    private var friendsListAdapter: FriendsListAdapter? = null
    private var friendsListItems: ArrayList<FriendsListItems>? = null

    var mSocket: Socket? = null
    private var recyclerViewState: Parcelable? = null

    override fun getContentView(): View {
        binding = FragmentFriendsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initUi() {

        try {

            val app: FestumApplicationClass = requireActivity().application as FestumApplicationClass
            mSocket = app.getMSocket()

            if (mSocket?.connected() == true) {

                getMessage()

            } else {

                mSocket?.connected()

            }

        }catch (e: Exception) {
            Log.e("Error: ", e.message.toString())
        }

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

            if (friendsListData != null) {

                friendsListItems = friendsListData

                friendsListData.sortByDescending { it.lastMessage?.updatedAt }

                friendsListAdapter = FriendsListAdapter(requireActivity(), friendsListData)
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

        mSocket?.on("newMessage") { args ->

            val data = args[0] as JSONObject

            activity?.runOnUiThread {

                friendsListAdapter?.updateItem(data)
                binding.chatRecyclerview.scrollToPosition(0)

            }

        }?.on("callUser"){ args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "getMessage: $data")

        }?.on("updateMyMedia"){ args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "updateMyMedia: $data")

        }?.on("answerCall"){ args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "answerCall: $data")

        }?.on("endCall"){ args ->


            Log.e("TAG", "endCall: ${args.asList()}")

        }

    }

}