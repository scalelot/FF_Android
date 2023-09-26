package com.festum.festumfield.verstion.firstmodule.screens.main.group

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.festum.festumfield.R
import com.festum.festumfield.databinding.ActivityHomeBinding
import com.festum.festumfield.databinding.ActivityNewGroupBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.adapters.AddGroupMembersAdapter
import com.festum.festumfield.verstion.firstmodule.screens.adapters.FriendsListAdapter
import com.festum.festumfield.verstion.firstmodule.screens.adapters.GroupMembersListAdapter
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.ChatPinInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.GroupInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.utils.DeviceUtils
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.ArrayList
import java.util.Locale

@AndroidEntryPoint
class NewGroupActivity : BaseActivity<FriendsListViewModel>() , GroupInterface{

    private lateinit var binding: ActivityNewGroupBinding
    private var friendsListAdapter: GroupMembersListAdapter? = null
    private var addMemberAdapter: AddGroupMembersAdapter? = null
    private var friendsListItems: ArrayList<FriendsListItems>? = null
    private val addMemberList = arrayListOf<FriendsListItems>()

    override fun getContentView(): View {
        binding = ActivityNewGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        binding.icBack.setOnClickListener {
            finish()
        }

        val friendListBody = FriendListBody(search = "", limit = Int.MAX_VALUE, page = 1)
        viewModel.friendsList(friendListBody)

    }

    override fun setupObservers() {

        viewModel.friendsListData.observe(this) { friendsListData ->

            val membersListItems = arrayListOf<FriendsListItems>()

            if (lifecycle.currentState == Lifecycle.State.RESUMED) {

                if (friendsListData != null) {

                    friendsListItems = friendsListData

                    friendsListData.forEach {
                        if (it.members.isNullOrEmpty()){
                            membersListItems.add(it)
                        }
                    }

                    Handler(Looper.getMainLooper()).postDelayed({

                        membersListItems.sortByDescending { item -> item.lastMessage?.updatedAt }

                        friendsListAdapter = GroupMembersListAdapter(this@NewGroupActivity, membersListItems, this)
                        binding.recyclerviewContactList.adapter = friendsListAdapter

                    },100)

                }

            }

        }

        binding.edtSearchText.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.ivClearText.visibility = View.GONE
                    binding.ivSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearText.visibility = View.VISIBLE
                    binding.ivSearch.visibility = View.GONE

                }
            }

            override fun afterTextChanged(s: Editable?) {
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
            DeviceUtils.hideKeyboard(this@NewGroupActivity)
        })

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
                Toast.makeText(this@NewGroupActivity, "No User Found..", Toast.LENGTH_SHORT).show()
            } else {
                friendsListAdapter?.filterList(filteredList)
            }

        }

    }

    override fun onAddMemberClick(items: FriendsListItems) {

        addMemberList.add(items)

        addMemberAdapter = AddGroupMembersAdapter(this@NewGroupActivity, addMemberList, this)
        binding.recyclerviewSelectedList.adapter = addMemberAdapter

    }

    override fun onRemoveMemberClick(items: FriendsListItems, memberList : ArrayList<FriendsListItems>) {

        friendsListAdapter
    }

}