package com.festum.festumfield.verstion.firstmodule.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ItemSendCallBinding
import com.festum.festumfield.databinding.ItemSendProductBinding
import com.festum.festumfield.databinding.ItemSentImageBinding
import com.festum.festumfield.databinding.ItemSentMessageBinding
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ListItem
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ListSection
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.DocsItem
import com.festum.festumfield.verstion.firstmodule.utils.DateTimeUtils
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class ChatMessageAdapter(
    private val context: Context,
    private var listItems: ArrayList<ListItem>,
    private var receiverUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val MONTHHEADER = 0
    private val DATAVIEW = 1
    private val IMAGEVIEW = 2
    private val VIDEOVIEW = 3
    private val CALLVIEW = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            DATAVIEW -> {

                ViewHolder1(ItemSentMessageBinding.inflate(LayoutInflater.from(parent.context)))

            }

            IMAGEVIEW -> {

                ViewHolder2(ItemSentImageBinding.inflate(LayoutInflater.from(parent.context)))

            }

            VIDEOVIEW -> {

                ViewHolder3(ItemSendProductBinding.inflate(LayoutInflater.from(parent.context)))

            }

            CALLVIEW -> {

                ViewHolder4(ItemSendCallBinding.inflate(LayoutInflater.from(parent.context)))

            }

            else -> {

                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_date_item_layout, parent, false)
                ViewHolder(view)

            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        when (viewHolder.itemViewType) {
            DATAVIEW -> {
                val holder = viewHolder as ViewHolder1
                holder.bind(position)
            }

            IMAGEVIEW -> {
                val holder = viewHolder as ViewHolder2
                holder.bind(position)
            }

            VIDEOVIEW -> {
                val holder = viewHolder as ViewHolder3
                holder.bind(position)
            }

            CALLVIEW -> {
                val holder = viewHolder as ViewHolder4
                holder.bind(position)
            }

            else -> {
                val holder = viewHolder as ViewHolder
                holder.bind(position)
            }
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var dateTV: TextView = itemView.findViewById(R.id.dateTV)

        fun bind(pos: Int) {
            val item = listItems[pos] as ListSection
            dateTV.text = item.title
        }

    }

    inner class ViewHolder1(private val messageBinding: ItemSentMessageBinding) :
        RecyclerView.ViewHolder(messageBinding.root) {

        fun bind(position: Int) {

            val item = listItems[position] as DocsItem


            if (item.from?.id.toString().uppercase() == AppPreferencesDelegates.get().channelId) {

                messageBinding.sendTxt.text = item.content?.text?.message
                messageBinding.sendTxtTime.text = item.createdAt?.convertToFormattedTime()
                if (item.status.equals("sent")) {
                    messageBinding.sendTxtSeen.setImageResource(R.drawable.ic_unseen)
                } else {
                    messageBinding.sendTxtSeen.setImageResource(R.drawable.ic_seen)
                }
                messageBinding.rlLeft.visibility = View.GONE
                messageBinding.rlRight.visibility = View.VISIBLE

            } else {

                messageBinding.reciveTxt.text = item.content?.text?.message
                messageBinding.reciveUserName.text = item.from?.fullName
                messageBinding.reciveTxtTime.text = item.createdAt?.convertToFormattedTime()
                messageBinding.rlRight.visibility = View.GONE
                messageBinding.rlLeft.visibility = View.VISIBLE
                Glide.with(context).load(Constans.Display_Image_URL + item.from?.profileimage)
                    .placeholder(R.drawable.ic_user).into(messageBinding.reciveImgUser)


            }

        }

    }

    inner class ViewHolder2(private val imageBinding: ItemSentImageBinding) :
        RecyclerView.ViewHolder(imageBinding.root) {


        fun bind(position: Int) {

            val item = listItems[position] as DocsItem


            if (item.from?.id.toString().uppercase() == AppPreferencesDelegates.get().channelId) {

                imageBinding.relativeLeft.visibility = View.GONE
                imageBinding.relativeRight.visibility = View.VISIBLE

                imageBinding.sendImgTime.text = item.createdAt?.convertToFormattedTime()

                val image = Constans.Display_Image_URL + item.content?.media?.path

                imageBinding.sendImage.setLargeImagePath(image)

                if (item.status.equals("sent")) {
                    imageBinding.sendImgSeen.setImageResource(R.drawable.ic_unseen)
                } else {
                    imageBinding.sendImgSeen.setImageResource(R.drawable.ic_seen)
                }


                if (item.content?.text?.message.isNullOrEmpty()) {
                    imageBinding.sendMessageText.visibility = View.GONE
                }

                imageBinding.sendMessageText.text = item.content?.text?.message

                Glide.with(context)
                    .load(image)
                    .placeholder(R.mipmap.ic_app_logo)
                    .into(imageBinding.sendImage)


            } else {

                imageBinding.reciveUserTxt.text = item.from?.fullName
                imageBinding.relativeRight.visibility = View.GONE
                imageBinding.relativeLeft.visibility = View.VISIBLE

                imageBinding.reciveImgTime.text = item.createdAt?.convertToFormattedTime()

                val image = Constans.Display_Image_URL + item.content?.media?.path

                imageBinding.reciveImg.setLargeImagePath(image)

                if (item.content?.text?.message.isNullOrEmpty()) {
                    imageBinding.reciveMessageText.visibility = View.GONE
                }

                imageBinding.reciveMessageText.text = item.content?.text?.message

                Glide.with(context)
                    .load(image)
                    .error(R.mipmap.ic_app_logo)
                    .into(imageBinding.reciveImg)

                Glide.with(context)
                    .load(Constans.Display_Image_URL + item.from?.profileimage)
                    .placeholder(R.drawable.ic_user)
                    .into(imageBinding.reciveUserImg)


            }

        }

    }

    inner class ViewHolder3(private val productBinding: ItemSendProductBinding) :
        RecyclerView.ViewHolder(productBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {

            val item = listItems[position] as DocsItem

            if (item.from?.id.toString().uppercase() == AppPreferencesDelegates.get().channelId) {

                productBinding.sendRelative.visibility = View.VISIBLE
                productBinding.reciveRelative.visibility = View.GONE

                productBinding.sendProname.text = item.content?.product?.productid?.name
                productBinding.sendProdes.text = item.content?.product?.productid?.description
                productBinding.sendMessage.text = item.content?.text?.message
                productBinding.sendProprice.text =
                    "$ " + item.content?.product?.productid?.price.toString()

                productBinding.sendProtime.text = item.createdAt?.convertToFormattedTime()

                if (item.status.equals("sent")) {
                    productBinding.sendProseen.setImageResource(R.drawable.ic_unseen)
                } else {
                    productBinding.sendProseen.setImageResource(R.drawable.ic_seen)
                }

                if (item.content?.product?.productid?.images?.isNotEmpty() == true) {

                    val image = item.content.product.productid.images[0]

                    Glide.with(context)
                        .load(image)
                        .placeholder(R.mipmap.ic_app_logo)
                        .into(productBinding.sendProImage)

                }

            } else {

                productBinding.sendRelative.visibility = View.GONE;
                productBinding.reciveRelative.visibility = View.VISIBLE;

                productBinding.recviceProUserName.text = item.from?.fullName
                productBinding.recviceProName.text = item.content?.product?.productid?.name
                productBinding.recviceDes.text = item.content?.product?.productid?.description
                productBinding.recviceMessage.text = item.content?.text?.message
                productBinding.recviceMessage.text =
                    "$ " + item.content?.product?.productid?.price.toString()

                productBinding.recviceProTime.text = item.createdAt?.convertToFormattedTime()

                if (item.content?.product?.productid?.images?.isNotEmpty() == true) {

                    val image = item.content.product.productid.images[0]

                    Glide.with(context)
                        .load(image)
                        .placeholder(R.mipmap.ic_app_logo)
                        .into(productBinding.recviceImage)

                    Glide.with(context)
                        .load(Constans.Display_Image_URL + item.from?.profileimage)
                        .placeholder(R.drawable.ic_user)
                        .into(productBinding.recviceProfilePic)
                }

            }

        }

    }

    inner class ViewHolder4(private val callBinding: ItemSendCallBinding) :
        RecyclerView.ViewHolder(callBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {

            val item = listItems[position] as DocsItem

            if (item.from?.id.toString().uppercase() == AppPreferencesDelegates.get().channelId) {

                callBinding.sendRelative.visibility = View.VISIBLE
                callBinding.reciveRelative.visibility = View.GONE

                /*if (item.callId?.isVideoCall == true){
                    callBinding.sendProname.text = "Video Call"
                    callBinding.sendCallImage.setImageResource(R.drawable.ic_video_calling);
                }else{
                    callBinding.sendProname.text = "Audio Call"
                    callBinding.sendCallImage.setImageResource(R.drawable.ic_audio_calling);
                }*/

                item.callId?.members?.forEach {

                    if (it?.memberid != AppPreferencesDelegates.get().channelId.lowercase()) {

                        when (it?.status) {

                            "initiated" -> {

                                if (item.callId.isVideoCall == true) {
                                    callBinding.sendProname.text = "Video Call"
                                    callBinding.sendCallImage.setImageResource(R.drawable.ic_video_calling);
                                    callBinding.sendProprice.text = "No Answer"
                                } else {
                                    callBinding.sendProname.text = "Voice Call"
                                    callBinding.sendCallImage.setImageResource(R.drawable.ic_audio_calling);
                                    callBinding.sendProprice.text = "No Answer"
                                }

                            }

                            "ringing" -> {

                                if (item.callId.isVideoCall == true) {
                                    callBinding.sendProname.text = "Video Call"
                                    callBinding.sendCallImage.setImageResource(R.drawable.ic_video_calling);
                                    callBinding.sendProprice.text = "No Answer"
                                } else {
                                    callBinding.sendProname.text = "Voice Call"
                                    callBinding.sendCallImage.setImageResource(R.drawable.ic_audio_calling);
                                    callBinding.sendProprice.text = "No Answer"
                                }

                            }

                            "accepted" -> {
                                if (item.callId.isVideoCall == true) {
                                    callBinding.sendProname.text = "Video Call"
                                    callBinding.sendCallImage.setImageResource(R.drawable.ic_video_calling);

                                    if (it.startedAt != null && it.endAt != null){
                                        val duration = calculateDuration(it.startedAt, it.endAt)
                                        callBinding.sendProprice.text = duration
                                    } else{
                                        callBinding.sendProprice.text = "No Answer"
                                    }



                                } else {
                                    callBinding.sendProname.text = "Voice Call"
                                    callBinding.sendCallImage.setImageResource(R.drawable.ic_audio_calling);

                                    if (it.startedAt != null && it.endAt != null){
                                        val duration = calculateDuration(it.startedAt, it.endAt)
                                        callBinding.sendProprice.text = duration
                                    } else{
                                        callBinding.sendProprice.text = "No Answer"
                                    }

                                }
                            }

                        }

                    } else {


                    }

                }

                callBinding.sendProtime.text = item.createdAt?.convertToFormattedTime()

                if (item.status.equals("sent")) {
                    callBinding.sendProseen.setImageResource(R.drawable.ic_unseen)
                } else {
                    callBinding.sendProseen.setImageResource(R.drawable.ic_seen)
                }


            } else {

                callBinding.sendRelative.visibility = View.GONE;
                callBinding.reciveRelative.visibility = View.VISIBLE;

                /*callBinding.recviceProUserName.text = item.from?.fullName
                callBinding.recviceProName.text = item.content?.product?.productid?.name
                callBinding.recviceProTime.text = item.content?.product?.productid?.description*/

                callBinding.recviceProTime.text = item.createdAt?.convertToFormattedTime()

                item.callId?.members?.forEach {

                    if (it?.memberid != AppPreferencesDelegates.get().channelId) {

                        when (it?.status) {

                            "initiated" -> {

                                if (item.callId.isVideoCall == true) {
                                    callBinding.recviceProName.text = "Video Call"
                                    callBinding.receiveCallImage.setImageResource(R.drawable.ic_video_calling);

                                } else {
                                    callBinding.recviceProName.text = "Voice Call"
                                    callBinding.receiveCallImage.setImageResource(R.drawable.ic_audio_calling);
                                }

                            }

                            "ringing" -> {

                                if (item.callId.isVideoCall == true) {
                                    callBinding.recviceProName.text = "Missed Video Call"
                                    callBinding.receiveCallImage.setImageResource(R.drawable.ic_video_calling);
                                    callBinding.recvicePrice.text = "Tap to call back"
                                } else {
                                    callBinding.recviceProName.text = "Missed Voice Call"
                                    callBinding.receiveCallImage.setImageResource(R.drawable.ic_audio_calling);
                                    callBinding.recvicePrice.text = "Tap to call back"
                                }

                            }

                            "accepted" -> {
                                if (item.callId.isVideoCall == true) {
                                    callBinding.recviceProName.text = "Video Call"
                                    callBinding.receiveCallImage.setImageResource(R.drawable.ic_video_calling);

                                    if (it.startedAt != null && it.endAt != null){
                                        val duration = calculateDuration(it.startedAt, it.endAt)
                                        callBinding.recvicePrice.text = duration
                                    } else{
                                        callBinding.recvicePrice.text = "No Answer"
                                    }

                                } else {
                                    callBinding.recviceProName.text = "Voice Call"
                                    callBinding.receiveCallImage.setImageResource(R.drawable.ic_audio_calling);

                                    if (it.startedAt != null && it.endAt != null){
                                        val duration = calculateDuration(it.startedAt, it.endAt)
                                        callBinding.recvicePrice.text = duration
                                    } else{
                                        callBinding.recvicePrice.text = "No Answer"
                                    }

                                }
                            }

                        }

                    }else{

                    }

                }

                Glide.with(context)
                    .load(Constans.Display_Image_URL + item.from?.profileimage)
                    .placeholder(R.drawable.ic_user)
                    .into(callBinding.recviceProfilePic)

            }

        }

    }

    fun addItem(item: ListItem) {

        listItems.add(item)
        notifyItemInserted(listItems.size)
    }

    override fun getItemCount(): Int = listItems.size

    override fun getItemViewType(position: Int): Int =
        if (listItems[position] is ListSection) MONTHHEADER
        else {
            val item = listItems[position] as DocsItem
            when (item.contentType) {
                "text" -> DATAVIEW
                "media" -> IMAGEVIEW
                "product" -> VIDEOVIEW
                "call" -> CALLVIEW
                else -> {
                    DATAVIEW
                }
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    fun clearAll() {
        this.listItems.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(
        chatsWithDate: List<ListItem>?
    ) {
        this.listItems = ArrayList(chatsWithDate ?: listOf())

        notifyDataSetChanged()
    }

    fun String.convertToFormattedTime(): String {
        val inputFormat = SimpleDateFormat(DateTimeUtils.FORMAT_API_DATETIME, Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()

        val parsedDate = inputFormat.parse(this)
        return parsedDate?.let { outputFormat.format(it) } ?: ""
    }

    fun updateItem(data: JSONObject) {

        /* Check user */
        val userId = data.optString("messageid").lowercase()


    }

    fun calculateDuration(startedAt: Long, endAt: Long): String {
        // Calculate the duration in milliseconds
        val durationInMillis = endAt - startedAt

        // Convert milliseconds to seconds and minutes
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) % 60
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis)

        // Format the duration as a string
        return String.format("%02d:%02d", minutes, seconds)
    }

}