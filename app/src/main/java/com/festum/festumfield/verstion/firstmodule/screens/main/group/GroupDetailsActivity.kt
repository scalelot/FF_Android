package com.festum.festumfield.verstion.firstmodule.screens.main.group

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ActivityGroupDetailsBinding
import com.festum.festumfield.databinding.ActivityNewGroupBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.adapters.GroupMembersListAdapter
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.GroupInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupDetailsActivity : BaseActivity<FriendsListViewModel>(), GroupInterface {

    private lateinit var binding: ActivityGroupDetailsBinding
    private lateinit var membersList: FriendsListItems
    private var friendsListAdapter: GroupMembersListAdapter? = null
    private val groupMembers = arrayListOf<FriendsListItems>()
    override fun getContentView(): View {
        binding = ActivityGroupDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun setupUi() {

        val intent = intent.extras

        val jsonList = intent?.getString("groupMembersList")

        membersList = Gson().fromJson(jsonList, FriendsListItems::class.java)

        val profileImage = Constans.Display_Image_URL + membersList.profileimage

        Glide.with(this@GroupDetailsActivity)
            .load(profileImage)
            .placeholder(R.drawable.ic_user)
            .into(binding.groupProfileImage)


        if (membersList.members?.isNotEmpty() == true) {

            membersList.members?.forEach {

                val members = FriendsListItems(
                    profileimage = it.membersList?.profileimage,
                    fullName = it.membersList?.fullName,
                    aboutUs = it.membersList?.aboutUs
                )
                groupMembers.add(members)
            }

            binding.groupName.text = membersList.name




            binding.txtPeople.text = membersList.members?.size.toString() + " peoples"

            Handler(Looper.getMainLooper()).postDelayed({
                friendsListAdapter = GroupMembersListAdapter(this@GroupDetailsActivity, groupMembers, this)
                binding.recyclerGroup.adapter = friendsListAdapter
            },100)

            binding.backArrow.setOnClickListener {
                finish()
            }
        }


    }

    override fun setupObservers() {


    }

    override fun onAddMemberClick(items: FriendsListItems, b: Boolean) {

    }

    override fun onRemoveMemberClick(items: FriendsListItems, position: Int) {

    }
}