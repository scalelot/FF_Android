package com.festum.festumfield.verstion.firstmodule.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.UserChatListBinding
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.GroupInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.GroupMembersInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.*
import com.festum.festumfield.verstion.firstmodule.utils.DateTimeUtils
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class EditGroupMembersListAdapter(
    private val context: Context,
    private var friendsList: ArrayList<GroupMembersListItems>,
    private val onAddMemberClick: GroupMembersInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var isExpanded = false

    @SuppressLint("NotifyDataSetChanged")
    fun toggleExpansion() {
        isExpanded = !isExpanded
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(UserChatListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (position != RecyclerView.NO_POSITION && position < friendsList.size) {
            val holder = viewHolder as ViewHolder
            holder.bind(position)
        }

    }

    inner class ViewHolder(private val binding: UserChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "NotifyDataSetChanged")
        fun bind(position: Int) {

            val item = friendsList[position]

            val image = Constans.Display_Image_URL + item.profileimage

            Glide.with(context)
                .load(image)
                .placeholder(R.drawable.ic_user)
                .into(binding.imgUser)

            binding.txtUserName.text = item.fullName

            if (item.fullName == null) {
                binding.txtUserName.text = item.name
            }

            binding.txtMessage.text = item.aboutUs

            binding.chatPin.visibility = View.GONE
            binding.ivTypeImg.visibility = View.GONE
            binding.txtChatCount.visibility = View.GONE
            binding.chatUserTime.visibility = View.GONE
            binding.txtChatCount.visibility = View.GONE

            binding.chatUserTime.text = item.lastMessage?.timestamp?.let { getTimeInMillis(it) }

            binding.txtChatCount.text = item.unreadMessageCount.toString()

            if (item.isNewMessage == true){
                binding.ivSelect.visibility = View.VISIBLE
            }else{
                binding.ivSelect.visibility = View.GONE
            }

            binding.rlChatLayout.setOnClickListener {

                onAddMemberClick.onRemoveMemberClick(item,position)

            }

        }

    }

    fun removeMember(friendsListItems: GroupMembersListItems?){

        val currentItemIndex = friendsList.indexOf(friendsListItems)
        notifyItemRemoved(currentItemIndex)
        friendsList.remove(friendsListItems)

        onAddMemberClick.onMembersList(friendsList.size)

    }

    override fun getItemCount(): Int = if (isExpanded || friendsList.size <= MIN_ITEMS_TO_SHOW) {
        friendsList.size
    } else {
        MIN_ITEMS_TO_SHOW
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterItems: ArrayList<FriendsListItems>) {
       /* friendsList = filterItems
        notifyDataSetChanged()*/
    }

    fun getTimeInMillis(smsTimeInMillis: Long): String {
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = smsTimeInMillis
        val now = Calendar.getInstance()
        val timeFormatString = "hh:mm aa"
        val dateTimeFormatString = "dd/MM/yyyy"
        val dayFormatString = "EEEE"
        return if (now[Calendar.DATE] == smsTime[Calendar.DATE]) {
            "" + DateFormat.format(timeFormatString, smsTime)
        } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
            "Yesterday"
        } else if (
            now[Calendar.DATE] - smsTime[Calendar.DATE] >= 2 || now[Calendar.DATE] - smsTime[Calendar.DATE] <= 6) {
            "" + DateFormat.format(dayFormatString, smsTime)
        } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
            DateFormat.format(dateTimeFormatString, smsTime).toString()
        } else {
            DateFormat.format("MM-dd-yyyy", smsTime).toString()
        }
    }

    private fun getCurrentUTCTime(): String {
        val nowInUtc = OffsetDateTime.now(ZoneOffset.UTC)
        nowInUtc.format(DateTimeFormatter.ofPattern(DateTimeUtils.FORMAT_API_DATETIME))
        return nowInUtc.toString()
    }

    companion object {
        private const val MIN_ITEMS_TO_SHOW = 5
    }

}