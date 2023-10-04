package com.festum.festumfield.verstion.firstmodule.screens.dialog

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import com.festum.festumfield.databinding.AddMembersItemsBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseDialogFragment
import com.festum.festumfield.verstion.firstmodule.screens.adapters.AddMembersListAdapter
import com.festum.festumfield.verstion.firstmodule.screens.adapters.GroupMembersListAdapter
import com.festum.festumfield.verstion.firstmodule.sources.local.model.FriendListBody
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.GroupInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsListItems
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendProductViewModel
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendsListViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMembersDialog(private val membersList: FriendsListItems) :
    BaseDialogFragment<FriendsListViewModel>(),
    GroupInterface {

    private lateinit var binding: AddMembersItemsBinding
    private var friendsListAdapter: AddMembersListAdapter? = null
    private var membersListItems = arrayListOf<FriendsListItems>()

    override fun getContentView(): View {
        binding = AddMembersItemsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initUi() {

        val friendListBody = FriendListBody(search = "", limit = Int.MAX_VALUE, page = 1)
        viewModel.friendsList(friendListBody)

        binding.icBack.setOnClickListener {
            dismiss()
        }


    }

    override fun setObservers() {

        viewModel.friendsListData.observe(this) { friendsListData ->


            if (lifecycle.currentState == Lifecycle.State.RESUMED) {

                if (friendsListData != null) {

                    friendsListData.forEach {friends ->

                        if (friends.members.isNullOrEmpty()){
                            membersListItems.add(friends)
                        }

                    }



                    Handler(Looper.getMainLooper()).postDelayed({

                        friendsListAdapter = AddMembersListAdapter(requireActivity(), membersListItems, this)
                        binding.recyclerviewContactList.adapter = friendsListAdapter

                    }, 100)

                }

            }

        }


    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it1 ->
                val behaviour = BottomSheetBehavior.from(it1)
                setupFullHeight(it1)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.isDraggable = false
            }
        }

        return dialog

    }

    private fun setupFullHeight(bottomSheet: View) {

        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams

    }

    override fun onAddMemberClick(items: FriendsListItems, b: Boolean) {

    }

    override fun onRemoveMemberClick(items: FriendsListItems, position: Int) {

    }
}