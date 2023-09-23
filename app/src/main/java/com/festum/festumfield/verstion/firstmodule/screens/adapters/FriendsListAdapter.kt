package com.festum.festumfield.verstion.firstmodule.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.UserChatListBinding
import com.festum.festumfield.verstion.firstmodule.screens.main.chat.ChatActivity
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.ChatPinInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.*
import com.festum.festumfield.verstion.firstmodule.utils.DateTimeUtils
import com.google.gson.Gson
import org.json.JSONObject
import java.io.Serializable
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class FriendsListAdapter(
    private val context: Context,
    private var friendsList: ArrayList<FriendsListItems>,
    private val pinInterface: ChatPinInterface,
    private var isLongClick : Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(UserChatListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val holder = viewHolder as ViewHolder
        holder.bind(position)

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

            if (item.fullName == null){
                binding.txtUserName.text = item.name
            }

            if (item.isPinned == true){
                binding.chatPin.visibility = View.VISIBLE
            }else{
                binding.chatPin.visibility = View.INVISIBLE
            }

            if (item.isNewMessage == true) {
                binding.txtMessage.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorAccent
                    )
                );
                binding.txtChatCount.visibility = View.VISIBLE

                if (item.isPinned == true){
                    binding.chatPin.visibility = View.INVISIBLE
                }

            } else {
                binding.txtMessage.setTextColor(ContextCompat.getColor(context, R.color.grey));
                binding.txtChatCount.visibility = View.GONE
            }

            if (item.online == true){
                binding.ivTypeImg.visibility = View.VISIBLE
            }else{
                binding.ivTypeImg.visibility = View.INVISIBLE
            }

            if (item.lastMessage != null) {

                when (item.lastMessage.contentType) {
                    "text" -> {
                        binding.txtMessage.text = item.lastMessage.content?.text?.message
                        binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            null,
                            null
                        )
                    }

                    "media" -> {
                        binding.txtMessage.text = context.getString(R.string.image)
                        binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                            context.resources.getDrawable(R.drawable.ic_friends_image_vector),
                            null,
                            null,
                            null
                        )
                    }

                    "product" -> {
                        binding.txtMessage.text = context.getString(R.string.product)
                        binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                            context.resources.getDrawable(R.drawable.ic_friends_product_image),
                            null,
                            null,
                            null
                        )
                    }

                    "" -> {
                        binding.txtMessage.text = ""
                    }
                }

            }

            binding.chatUserTime.text = item.lastMessage?.timestamp?.let { getTimeInMillis(it) }

            binding.txtChatCount.text = item.messageSize

            when (checkedPosition) {
                -1 -> {
                    binding.ivSelect.visibility = View.GONE
                }
                else -> when (checkedPosition) {
                    absoluteAdapterPosition -> {
                        binding.ivSelect.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.ivSelect.visibility = View.GONE
                    }
                }
            }

            binding.rlChatLayout.setOnClickListener {

                if (isLongClick) {

                    binding.ivSelect.visibility = View.VISIBLE
                    if (checkedPosition != absoluteAdapterPosition) {

                        notifyItemChanged(checkedPosition)
                        checkedPosition = absoluteAdapterPosition
                    }

                    pinInterface.checkItemPin(friendsList[absoluteAdapterPosition])

                } else  {
                    val intent = Intent(context, ChatActivity::class.java)
                    val jsonItem = Gson().toJson(item)
                    intent.putExtra("friendsList", jsonItem)
                    context.startActivity(intent)
                }

            }

            binding.rlChatLayout.setOnLongClickListener {
                isLongClick = true
                pinInterface.checkItemPin(item)
                false
            }

        }

    }

    override fun getItemCount(): Int = friendsList.size

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterItems: ArrayList<FriendsListItems>) {
        friendsList = filterItems
        notifyDataSetChanged()
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

    @SuppressLint("NotifyDataSetChanged")
    fun updateItem(data: JSONObject) {

        /* Check user */
        val userId = data.optString("from").lowercase()

        var friendsListItems: FriendsListItems? = null
        friendsList.forEach {
            if (userId == it.id) {
                friendsListItems = it
            }
        }

        val contentList = data.getJSONObject("content")
        val messageList = contentList.getJSONObject("text")
        val mediaList = contentList.getJSONObject("media")
        val product = contentList.getJSONObject("product")

        val text = Text(messageList.optString("message"))
        val media = Media(
            path = mediaList.optString("path"),
            mime = mediaList.optString("mime"),
            name = mediaList.optString("name"),
            type = mediaList.optString("type")
        )

        val productItem = Product(ProductItem(id = product.optString("productid")))

        val lastMessageItemList = LastMessageItem(
            timestamp = data.optLong("timestamp"),
            content = Content(text = text, media = media, product = productItem),
            contentType = data.optString("contentType")
        )

        val friendsListItem = FriendsListItems(
            fullName = friendsListItems?.fullName,
            lastMessage = lastMessageItemList,
            profileimage = friendsListItems?.profileimage,
            id = friendsListItems?.id,
            createdAt = getCurrentUTCTime(),
            isPinned = friendsListItems?.isPinned,
            isNewMessage = true,
            messageSize = "1",
            online = friendsListItems?.online
        )

        val currentItemIndex = friendsList.indexOf(friendsListItems)
        friendsList.removeAt(currentItemIndex)
        friendsListItem.let { friendsList.add(0, it) }
        notifyItemMoved(currentItemIndex, 0)
        notifyItemChanged(0)

    }
    fun updateOnline(data: JSONObject?) {

        val onlineUserChannelId = data?.keys()
        var friendsListItems: FriendsListItems? = null

        while (onlineUserChannelId?.hasNext() == true) {
            val key = onlineUserChannelId.next()

            friendsList.forEach {
                if (key.toString().lowercase() == it.id) {
                    friendsListItems = it
                }
            }

        }

        val friendsListItem = FriendsListItems(
            fullName = friendsListItems?.fullName,
            lastMessage = friendsListItems?.lastMessage,
            profileimage = friendsListItems?.profileimage,
            id = friendsListItems?.id,
            isPinned = friendsListItems?.isPinned,
            online = true
        )

        if (friendsListItems != null){

            val currentItemIndex = friendsList.indexOf(friendsListItems)

            friendsList.removeAt(currentItemIndex)
            friendsListItem.let { friendsList.add(0, it) }
            notifyItemMoved(currentItemIndex, 0)
            notifyItemChanged(0)

        }

    }

    fun updateOffline(receiverChannelId: String?) {

        var friendsListItems: FriendsListItems? = null

        friendsList.forEach {
            if (receiverChannelId.toString().lowercase() == it.id) {
                friendsListItems = it
            }
        }

        val friendsListItem = FriendsListItems(
            fullName = friendsListItems?.fullName,
            lastMessage = friendsListItems?.lastMessage,
            profileimage = friendsListItems?.profileimage,
            id = friendsListItems?.id,
            isPinned = friendsListItems?.isPinned,
            online = false
        )

        if (friendsListItems != null){

            val currentItemIndex = friendsList.indexOf(friendsListItems)
            friendsList.removeAt(currentItemIndex)
            friendsListItem.let { friendsList.add(currentItemIndex, it) }
            notifyItemChanged(currentItemIndex)

        }

    }

    fun updatePin(friendData: FriendsListItems?, pinSet: Boolean){

        val userId = friendData?.id

        var friendsListItems: FriendsListItems? = null
        friendsList.forEach {
            if (userId == it.id) {
                friendsListItems = it
            }
        }

        val friendsListItem = FriendsListItems(
            fullName = friendsListItems?.fullName,
            lastMessage = friendsListItems?.lastMessage,
            profileimage = friendsListItems?.profileimage,
            id = friendsListItems?.id,
            isPinned = pinSet,
        )

        if (friendsListItems != null){

            if (pinSet){
                val currentItemIndex = friendsList.indexOf(friendsListItems)
                friendsList.removeAt(currentItemIndex)
                friendsList.add(0, friendsListItem)
                notifyItemMoved(currentItemIndex, 0)
                notifyItemChanged(0)
            } else {
                val currentItemIndex = friendsList.indexOf(friendsListItems)
                friendsList.removeAt(currentItemIndex)
                friendsList.add(friendsList.size, friendsListItem)
                notifyItemMoved(currentItemIndex, friendsList.size)
                notifyItemChanged(friendsList.size)
            }

            isLongClick = false

        }

    }

    fun pinNotSelected(itemData: FriendsListItems?) {

        isLongClick = false

        val userId = itemData?.id

        var friendsListItems: FriendsListItems? = null
        friendsList.forEach {
            if (userId == it.id) {
                friendsListItems = it
            }
        }
        val friendsListItem = FriendsListItems(
            fullName = friendsListItems?.fullName,
            lastMessage = friendsListItems?.lastMessage,
            profileimage = friendsListItems?.profileimage,
            id = friendsListItems?.id,
            isPinned = friendsListItems?.isPinned
        )

        if (friendsListItems != null){

            checkedPosition = -1
            val currentItemIndex = friendsList.indexOf(friendsListItems)
            friendsList.removeAt(currentItemIndex)
            friendsListItem.let { friendsList.add(currentItemIndex, it) }
            notifyItemChanged(currentItemIndex)

        }


    }

    private fun getCurrentUTCTime(): String {
        val nowInUtc = OffsetDateTime.now(ZoneOffset.UTC)
        nowInUtc.format(DateTimeFormatter.ofPattern(DateTimeUtils.FORMAT_API_DATETIME))
        return nowInUtc.toString()
    }

}