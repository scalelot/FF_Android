package com.festum.festumfield.verstion.firstmodule.screens.main.group

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.festum.festumfield.databinding.ActivityNewGroupBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.adapters.AddGroupMembersAdapter
import com.festum.festumfield.verstion.firstmodule.screens.adapters.GroupMembersListAdapter
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.GroupInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.utils.DeviceUtils
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class NewGroupActivity : BaseActivity<FriendsListViewModel>() , GroupInterface{

    private lateinit var binding: ActivityNewGroupBinding
    private var friendsListAdapter: GroupMembersListAdapter? = null
    private var addMemberAdapter: AddGroupMembersAdapter? = null
    private var membersListItems = arrayListOf<FriendsListItems>()
    private val addMemberList = arrayListOf<FriendsListItems>()

    override fun getContentView(): View {
        binding = ActivityNewGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        binding.icBack.setOnClickListener {
            finish()

        }

        val friendListBody = FriendListBody(search = "", limit = Int.MAX_VALUE, page = 1, isGroup = true)
        viewModel.friendsList(friendListBody)

        binding.recyclerviewSelectedList.visibility = View.GONE
        DeviceUtils.hideKeyboard(this@NewGroupActivity)

    }

    @SuppressLint("SetTextI18n")
    override fun setupObservers() {

        viewModel.friendsListData.observe(this) { friendsListData ->



            if (lifecycle.currentState == Lifecycle.State.RESUMED) {

                if (friendsListData != null) {

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

                    binding.txtSelectedCount.text = addMemberList.size.toString() + " of "
                    binding.txtTotalCount.text = membersListItems.size.toString() + " selected "

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

        binding.ivNext.setOnClickListener {

            if (addMemberList.isEmpty()) {
                Snackbar.make(binding.linear, "Group Members not found", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val gson = Gson()

            val membersItems = gson.toJson(addMemberList)

            val intent = Intent(this@NewGroupActivity, CreateGroupActivity::class.java)
            intent.putExtra("memberList", membersItems)
            startActivity(intent)

        }

    }

    private fun filter(text: String) {

        val filteredList = ArrayList<FriendsListItems>()

        if (membersListItems.isNotEmpty()) {

            membersListItems.forEach { item ->
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

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")

    override fun onAddMemberClick(
        items: FriendsListItems,
        b: Boolean,
        addMembersList: ArrayList<String>
    ) {
        if (b){
            addMemberList.add(items)
        }else{
            addMemberList.remove(items)
        }

        addMemberAdapter = AddGroupMembersAdapter(this@NewGroupActivity, addMemberList, this)
        binding.recyclerviewSelectedList.adapter = addMemberAdapter

        friendsListAdapter?.notifyDataSetChanged()
        binding.txtSelectedCount.text = addMemberList.size.toString() + " of "
        binding.txtTotalCount.text = membersListItems.size.toString() + " selected "
        if (addMemberList.isNullOrEmpty()){
            binding.recyclerviewSelectedList.visibility = View.GONE
        }else{
            binding.recyclerviewSelectedList.visibility = View.VISIBLE
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onRemoveMemberClick(items: FriendsListItems, position : Int) {
//        membersListItems[membersListItems.indexOf(items)].isNewMessage = false

        addMemberList.remove(items)

        addMemberAdapter = AddGroupMembersAdapter(this@NewGroupActivity, addMemberList, this)
        binding.recyclerviewSelectedList.adapter = addMemberAdapter

        friendsListAdapter?.updateOffline(items.id)

        binding.txtSelectedCount.text = addMemberList.size.toString() + " of "
        binding.txtTotalCount.text = membersListItems.size.toString() + " selected "

        if (addMemberList.isNullOrEmpty()){
            binding.recyclerviewSelectedList.visibility = View.GONE
        }else{
            binding.recyclerviewSelectedList.visibility = View.VISIBLE
        }

    }

    override fun onMembersList(position: Int) {

    }

    override fun onRemoveMember(items: FriendsListItems) {

    }

}