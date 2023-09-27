package com.festum.festumfield.verstion.firstmodule.screens.main.group

import android.view.View
import com.festum.festumfield.databinding.ActivityCreateGroupBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.adapters.AddGroupMembersAdapter
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.GroupInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateGroupActivity : BaseActivity<FriendsListViewModel>() , GroupInterface {

    private lateinit var binding: ActivityCreateGroupBinding
    private var membersList = arrayListOf<FriendsListItems>()
    private var addMemberAdapter: AddGroupMembersAdapter? = null

    override fun getContentView(): View {
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        val members = intent?.getSerializableExtra("memberList") as ArrayList<FriendsListItems>

        addMemberAdapter =  AddGroupMembersAdapter(this@CreateGroupActivity, members, this)
        binding.recySelectedList.adapter = addMemberAdapter

    }

    override fun setupObservers() {

    }

    override fun onAddMemberClick(items: FriendsListItems, b: Boolean) {

    }

    override fun onRemoveMemberClick(items: FriendsListItems, position: Int) {

    }
}