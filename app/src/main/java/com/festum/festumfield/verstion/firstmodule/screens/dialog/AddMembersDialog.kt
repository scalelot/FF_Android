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
import com.festum.festumfield.databinding.AddMembersItemsBinding
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
class AddMembersDialog(private val membersList: FriendsListItems,
    private val onClick : GroupInterface) :
    BaseDialogFragment<FriendsListViewModel>(),
    GroupInterface {

    private lateinit var binding: AddMembersItemsBinding
    private var friendsListAdapter: AddMembersListAdapter? = null
    private var membersListItems = arrayListOf<FriendsListItems>()
    private var addMembersListItems = arrayListOf<FriendsListItems>()
    private val addMemberList = arrayListOf<FriendsListItems>()
    private var addMembersList = arrayListOf<String>()

    override fun getContentView(): View {
        binding = AddMembersItemsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initUi() {

        membersList.members?.forEach {

            it.membersList?.id?.let { it1 -> addMembersList.add(it1) }

        }

        val friendListBody = FriendListBody(search = "", limit = Int.MAX_VALUE, page = 1)
        viewModel.friendsList(friendListBody)

        binding.icBack.setOnClickListener {
            dismiss()
        }

        binding.ivNext.setOnClickListener {

            if (addMemberList.isEmpty()) {
                Snackbar.make(binding.linear, "Group Members not found", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val membersIds = arrayListOf<String>()
            addMemberList.forEach {
                it.id?.let { it1 -> membersIds.add(it1) }
            }

            val addMembers = CreateGroupBody(
                groupid = membersList.id,
                members = membersIds
            )

            viewModel.addMembers(addMembers)

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

                    membersListItems.forEach {

                        if (!addMembersList.contains(it.id)){

                            addMembersListItems.add(it)

                        }

                    }


                    Handler(Looper.getMainLooper()).postDelayed({

                        friendsListAdapter = AddMembersListAdapter(requireActivity(), addMembersListItems, this)
                        binding.recyclerviewContactList.adapter = friendsListAdapter

                    }, 100)

                }

            }

        }

        binding.edtSearchText.addTextChangedListener(object : TextWatcher {

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
            DeviceUtils.hideKeyboard(requireActivity())
        })

        viewModel.addMembersData.observe(this){

            if (it != null){

                onClick.onAddMemberClick(FriendsListItems(),true)
                dismiss()

            }else{

                Toast.makeText(requireActivity(), "Something get wrong", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireActivity(), "No User Found..", Toast.LENGTH_SHORT).show()
            } else {
                friendsListAdapter?.filterList(filteredList)
            }

        }

    }


    override fun onAddMemberClick(items: FriendsListItems, b: Boolean) {
        if (b){
            addMemberList.add(items)
        }else{
            addMemberList.remove(items)
        }
    }

    override fun onRemoveMemberClick(items: FriendsListItems, position: Int) {

    }

    override fun onMembersList(position: Int) {

    }

    override fun onRemoveMember(items: FriendsListItems) {

    }

}