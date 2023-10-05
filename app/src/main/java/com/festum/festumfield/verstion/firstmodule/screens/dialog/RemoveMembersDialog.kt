package com.festum.festumfield.verstion.firstmodule.screens.dialog

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.AddMembersItemsBinding
import com.festum.festumfield.databinding.RemoveMemberItemBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseDialogFragment
import com.festum.festumfield.verstion.firstmodule.screens.adapters.AddMembersListAdapter
import com.festum.festumfield.verstion.firstmodule.screens.adapters.GroupMembersListAdapter
import com.festum.festumfield.verstion.firstmodule.sources.local.model.CreateGroupBody
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.GroupInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProfileResponse
import com.festum.festumfield.verstion.firstmodule.utils.DeviceUtils
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendProductViewModel
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

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


    override fun onAddMemberClick(items: FriendsListItems, b: Boolean) {
    }

    override fun onRemoveMemberClick(items: FriendsListItems, position: Int) {

    }

    override fun onMembersList(position: Int) {

    }

    override fun onRemoveMember(items: FriendsListItems) {

    }

}