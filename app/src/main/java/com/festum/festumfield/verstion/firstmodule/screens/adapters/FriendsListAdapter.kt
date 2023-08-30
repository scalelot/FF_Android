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
import com.festum.festumfield.verstion.firstmodule.screens.main.ChatActivity
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.*
import com.festum.festumfield.verstion.firstmodule.utils.DateTimeUtils
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class FriendsListAdapter(
    private val context: Context,
    private var friendsList: ArrayList<FriendsListItems>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

            if (item.isNewMessage == true){
                binding.txtMessage.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                binding.txtChatCount.visibility = View.VISIBLE
            }else{
                binding.txtMessage.setTextColor(ContextCompat.getColor(context, R.color.grey));
                binding.txtChatCount.visibility = View.GONE
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

            binding.rlChatLayout.setOnClickListener {

                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("userName", item.fullName)
                intent.putExtra("userImage", item.profileimage)
                intent.putExtra("id", item.id)
                context.startActivity(intent)

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

    /*fun updateItem(optString: String) {



        friendsList.forEach {
            if (optString == it.id){
                Log.e("TAG", "updateItem: $optString")
                notifyItemRangeChanged(0, friendsList.size)
            }
        }

    }*/

    @SuppressLint("NotifyDataSetChanged")
    fun updateItem(data: JSONObject) {

        /* Check user */
        val userId = data.optString("from").lowercase()

        var friendsListItems : FriendsListItems ?= null
        friendsList.forEach {
            if (userId == it.id) {
                friendsListItems = it
            }
        }


        val contentList = data.getJSONObject("content")
        val messageList = contentList.getJSONObject("text")
        val mediaList = contentList.getJSONObject("media")
        val product = contentList.getJSONObject("product")

        val text = SendText(messageList.optString("message"))
        val media = SendMedia(
            path = mediaList.optString("path"),
            mime = mediaList.optString("mime"),
            name = mediaList.optString("name"),
            type = mediaList.optString("type")
        )

        val productItem = SendProduct(productid = product.optString("productid"))

        val lastMessageItemList = LastMessageItem(
            timestamp = data.optLong("timestamp"),
            content = SendMessageContent(text = text, media = media, product = productItem),
            contentType = data.optString("contentType"))

        val friendsListItem = FriendsListItems(
            fullName = friendsListItems?.fullName,
            lastMessage = lastMessageItemList,
            profileimage = friendsListItems?.profileimage,
            id = friendsListItems?.id,
            createdAt = getCurrentUTCTime(),
            isNewMessage = true,
            messageSize = "1",
        )

        val currentItemIndex = friendsList.indexOf(friendsListItems)
        friendsList.removeAt(currentItemIndex)
        friendsListItem.let { friendsList.add(0, it) }
        notifyItemMoved(currentItemIndex, 0)
        notifyItemChanged(0)

    }

    private fun getCurrentUTCTime() : String {
        val nowInUtc = OffsetDateTime.now(ZoneOffset.UTC)
        nowInUtc.format(DateTimeFormatter.ofPattern(DateTimeUtils.FORMAT_API_DATETIME))
        Log.e("TAG", "getCurrentUTCTime:--- $nowInUtc")
        return nowInUtc.toString()
    }

}