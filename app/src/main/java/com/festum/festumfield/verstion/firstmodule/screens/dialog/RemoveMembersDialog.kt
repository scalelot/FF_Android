package com.festum.festumfield.verstion.firstmodule.screens.dialog

import android.view.View
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.RemoveMemberItemBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseDialogFragment
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.GroupInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoveMembersDialog(private val membersList: FriendsListItems,
                          private val onClick : GroupInterface) :
    BaseDialogFragment<FriendsListViewModel>(),
    GroupInterface {

    private lateinit var binding: RemoveMemberItemBinding
    private var addMembersList = arrayListOf<String>()

    override fun getContentView(): View {
        binding = RemoveMemberItemBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initUi() {

        val profileImage = Constans.Display_Image_URL + membersList.profileimage

        Glide.with(requireActivity())
            .load(profileImage)
            .placeholder(R.drawable.ic_user)
            .into(binding.imgUser)

        binding.txtUserName.text = membersList.fullName

        binding.chatUserTime.setOnClickListener {
            dismiss()
        }

        binding.removeMember.setOnClickListener {

            onClick.onRemoveMember(membersList)
            dismiss()

        }


    }

    override fun setObservers() {


    }


    override fun onAddMemberClick(
        items: FriendsListItems,
        b: Boolean,
        addMembersList: ArrayList<String>
    ) {
    }

    override fun onRemoveMemberClick(items: FriendsListItems, position: Int) {

    }

    override fun onMembersList(position: Int) {

    }

    override fun onRemoveMember(items: FriendsListItems) {

    }

}