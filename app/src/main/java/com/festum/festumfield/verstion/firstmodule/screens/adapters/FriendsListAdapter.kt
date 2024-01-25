package com.festum.festumfield.verstion.firstmodule.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.util.Log
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
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.ChatPinInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.*
import com.festum.festumfield.verstion.firstmodule.utils.DateTimeUtils
import com.google.gson.Gson
import org.json.JSONObject
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class FriendsListAdapter(
    private val context: Context,
    private var friendsList: ArrayList<FriendsListItems>,
    private val pinInterface: ChatPinInterface,
    private var isLongClick: Boolean
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

            if (item.fullName == null) {
                binding.txtUserName.text = item.name
            }

            if (item.fullName == null && item.name == null) {
                binding.txtUserName.text = item.contactNo
            }

            if (item.isPinned == true) {
                binding.chatPin.visibility = View.VISIBLE
            } else {
                binding.chatPin.visibility = View.INVISIBLE
            }

            if (item.unreadMessageCount == 0) {
                binding.txtChatCount.visibility = View.GONE
            } else {
                binding.txtChatCount.text = item.unreadMessageCount.toString()
                binding.txtMessage.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorAccent
                    )
                )
                binding.txtChatCount.visibility = View.VISIBLE
            }

            if (item.isNewMessage == true) {
                binding.txtMessage.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorAccent
                    )
                );
                binding.txtChatCount.visibility = View.VISIBLE
                binding.txtChatCount.text = item.unreadMessageCount.toString()

            } else {

                binding.txtMessage.setTextColor(ContextCompat.getColor(context, R.color.grey));

            }

            if (item.online == true) {
                binding.ivTypeImg.visibility = View.VISIBLE
            } else {
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

                    "call" -> {

                        item.lastMessage.callId?.members?.forEach {

                            if (it?.memberid != AppPreferencesDelegates.get().channelId) {

                                when (it?.status) {

                                    "initiated" -> {

                                        if (item.lastMessage.callId.isVideoCall == true) {
                                            binding.txtMessage.text = "Video Call"
                                            binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                                                context.resources.getDrawable(R.drawable.ic_friend_video_calling),
                                                null,
                                                null,
                                                null
                                            )

                                        } else {
                                            binding.txtMessage.text = "Voice Call"
                                            binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                                                context.resources.getDrawable(R.drawable.ic_friend_audio_calling),
                                                null,
                                                null,
                                                null
                                            )
                                        }

                                    }

                                    "ringing" -> {

                                        if (item.lastMessage.callId.isVideoCall == true) {
                                            binding.txtMessage.text = "Missed Video Call"
                                            binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                                                context.resources.getDrawable(R.drawable.ic_friend_video_calling),
                                                null,
                                                null,
                                                null
                                            )
                                        } else {
                                            binding.txtMessage.text = "Missed Voice Call"
                                            binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                                                context.resources.getDrawable(R.drawable.ic_friend_audio_calling),
                                                null,
                                                null,
                                                null
                                            )
                                        }

                                    }

                                    "accepted" -> {
                                        if (item.lastMessage.callId.isVideoCall == true) {
                                            binding.txtMessage.text = "Video Call"
                                            binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                                                context.resources.getDrawable(R.drawable.ic_friend_video_calling),
                                                null,
                                                null,
                                                null
                                            )

                                        } else {
                                            binding.txtMessage.text = "Voice Call"
                                            binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                                                context.resources.getDrawable(R.drawable.ic_friend_audio_calling),
                                                null,
                                                null,
                                                null
                                            )

                                        }
                                    }

                                }

                            } else {

                                Log.e("TAG", "call user: " + "Something get wrong" )

                            }

                        }

                    }

                }

            }

            binding.chatUserTime.text = item.lastMessage?.timestamp?.let { getTimeInMillis(it) }

            if (item.members?.isNotEmpty() == true) {
                binding.chatUserTime.text =
                    item.lastChatMessage?.timestamp?.let { getTimeInMillis(it) }

                if (item.lastChatMessage != null) {

                    when (item.lastChatMessage?.message?.contentType) {
                        "text" -> {
                            binding.txtMessage.text =
                                item.lastChatMessage?.message?.content?.text?.message
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

                        "call" -> {

                            item.lastMessage?.callId?.members?.forEach {

                                if (it?.memberid != AppPreferencesDelegates.get().channelId) {

                                    when (it?.status) {

                                        "initiated" -> {

                                            if (item.lastMessage.callId.isVideoCall == true) {
                                                binding.txtMessage.text = "Video Call"
                                                binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                                                    context.resources.getDrawable(R.drawable.ic_friend_video_calling),
                                                    null,
                                                    null,
                                                    null
                                                )

                                            } else {
                                                binding.txtMessage.text = "Voice Call"
                                                binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                                                    context.resources.getDrawable(R.drawable.ic_friend_audio_calling),
                                                    null,
                                                    null,
                                                    null
                                                )
                                            }

                                        }

                                        "ringing" -> {

                                            if (item.lastMessage.callId.isVideoCall == true) {
                                                binding.txtMessage.text = "Missed Video Call"
                                                binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                                                    context.resources.getDrawable(R.drawable.ic_friend_video_calling),
                                                    null,
                                                    null,
                                                    null
                                                )
                                            } else {
                                                binding.txtMessage.text = "Missed Voice Call"
                                                binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                                                    context.resources.getDrawable(R.drawable.ic_friend_audio_calling),
                                                    null,
                                                    null,
                                                    null
                                                )
                                            }

                                        }

                                        "accepted" -> {
                                            if (item.lastMessage.callId.isVideoCall == true) {
                                                binding.txtMessage.text = "Video Call"
                                                binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                                                    context.resources.getDrawable(R.drawable.ic_friend_video_calling),
                                                    null,
                                                    null,
                                                    null
                                                )

                                            } else {
                                                binding.txtMessage.text = "Voice Call"
                                                binding.txtMessage.setCompoundDrawablesWithIntrinsicBounds(
                                                    context.resources.getDrawable(R.drawable.ic_friend_audio_calling),
                                                    null,
                                                    null,
                                                    null
                                                )

                                            }
                                        }

                                    }

                                } else {

                                    Log.e("TAG", "call user: " + "Something get wrong" )

                                }

                            }

                        }
                    }

                }
            }



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

                        notifyItemChanged(checkedPosition,item)
                        checkedPosition = absoluteAdapterPosition

                    }

                    pinInterface.checkItemPin(friendsList[absoluteAdapterPosition])

                } else {
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
            unreadMessageCount = friendsListItems?.unreadMessageCount?.plus(1),
            online = friendsListItems?.online,
            name = friendsListItems?.name,
            timestamp = friendsListItems?.timestamp
        )

        val currentItemIndex = friendsList.indexOf(friendsListItems)
        friendsList.removeAt(currentItemIndex)
        friendsListItem.let { friendsList.add(0, it) }
        notifyItemMoved(currentItemIndex, 0)
        notifyItemChanged(0)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateGroupItem(data: JSONObject, groupId: String) {

        /* Check user */

        var friendsListItems: FriendsListItems? = null
        friendsList.forEach {
            if (groupId == it.id) {
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

        val groupLastMessage = GroupMessageItem(
            LastMessageItem(
                timestamp = data.optLong("timestamp"),
                content = Content(text = text, media = media, product = productItem),
                contentType = data.optString("contentType")
            ),
            timestamp = data.optLong("timestamp")
        )


        val friendsListItem = FriendsListItems(
            fullName = friendsListItems?.fullName,
            lastMessage = lastMessageItemList,
            profileimage = friendsListItems?.profileimage,
            id = friendsListItems?.id,
            createdAt = getCurrentUTCTime(),
            isPinned = friendsListItems?.isPinned,
            isNewMessage = true,
            unreadMessageCount = friendsListItems?.unreadMessageCount?.plus(1),
            members = friendsListItems?.members,
            online = friendsListItems?.online,
            name = friendsListItems?.name,
            lastChatMessage = groupLastMessage
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
            online = true,
            members = friendsListItems?.members,
            unreadMessageCount = friendsListItems?.unreadMessageCount
        )

        if (friendsListItems != null) {

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
            online = false,
            members = friendsListItems?.members,
            unreadMessageCount = friendsListItems?.unreadMessageCount
        )

        if (friendsListItems != null) {

            val currentItemIndex = friendsList.indexOf(friendsListItems)
            friendsList.removeAt(currentItemIndex)
            friendsListItem.let { friendsList.add(currentItemIndex, it) }
            notifyItemChanged(currentItemIndex)

        }

    }

    fun updatePin(friendData: FriendsListItems?, pinSet: Boolean) {

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
            members = friendsListItems?.members,
            unreadMessageCount = friendsListItems?.unreadMessageCount
        )

        if (friendsListItems != null) {

            if (pinSet) {
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
            isPinned = friendsListItems?.isPinned,
            members = friendsListItems?.members
        )

        if (friendsListItems != null) {

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

    fun updateItemMessageSeen(userId: String) {

        var friendsListItems: FriendsListItems? = null
        friendsList.forEach {
            if (userId == it.id) {
                friendsListItems = it
            }
        }

        val lastMessageItemList = LastMessageItem(
            createdAt = friendsListItems?.lastMessage?.createdAt,
            v = friendsListItems?.lastMessage?.v,
            context = friendsListItems?.lastMessage?.context,
            from = friendsListItems?.lastMessage?.from,
            id = friendsListItems?.lastMessage?.id,
            to = friendsListItems?.lastMessage?.to,
            status = friendsListItems?.lastMessage?.status,
            updatedAt = friendsListItems?.lastMessage?.updatedAt,
            callId = friendsListItems?.lastMessage?.callId,
            timestamp = friendsListItems?.lastMessage?.timestamp,
            content = friendsListItems?.lastMessage?.content,
            contentType = friendsListItems?.lastMessage?.contentType
        )

        val friendsListItem = FriendsListItems(
            fullName = friendsListItems?.fullName,
            lastMessage = lastMessageItemList,
            profileimage = friendsListItems?.profileimage,
            id = friendsListItems?.id,
            createdAt = getCurrentUTCTime(),
            isPinned = friendsListItems?.isPinned,
            isNewMessage = friendsListItems?.isNewMessage,
            unreadMessageCount = 0,
            online = friendsListItems?.online,
            name = friendsListItems?.name,
            timestamp = friendsListItems?.timestamp
        )

        val currentItemIndex = friendsList.indexOf(friendsListItems)
        friendsList.removeAt(currentItemIndex)
        friendsListItem.let { friendsList.add(0, it) }
        notifyItemMoved(currentItemIndex, 0)
        notifyItemChanged(0)

    }

}