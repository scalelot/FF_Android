package com.festum.festumfield.verstion.firstmodule.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.Utils.Constans.Display_Image_URL
import com.festum.festumfield.databinding.ItemAllProductDisplayBinding
import com.festum.festumfield.databinding.ItemCallHistoryBinding
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.ProductItemInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.CallHistoryItem
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsProducts
import java.util.Calendar

class CallHistoryAdapter(
    private val context: Context,
    private val callHistoryItems : ArrayList<CallHistoryItem?> )
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemCallHistoryBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ViewHolder
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return callHistoryItems.size
    }

    inner class ViewHolder(private val displayBinding: ItemCallHistoryBinding) : RecyclerView.ViewHolder(displayBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val item = callHistoryItems[position]

            if (item?.initiatedBy?.id?.uppercase() == AppPreferencesDelegates.get().channelId.uppercase()){

                displayBinding.callImg.setImageResource(R.drawable.ic_call_outgoing)

            } else {

                displayBinding.callImg.setImageResource(R.drawable.ic_call_incoming);

            }

            if (AppPreferencesDelegates.get().channelId.uppercase() == item?.from?.id?.uppercase()){

                displayBinding.callUserName.text = item.to?.fullName

                val image = Display_Image_URL + item.to?.profileimage

                Glide.with(context)
                    .load(image)
                    .placeholder(R.drawable.ic_user)
                    .into(displayBinding.callUserProfile)

            } else {

                displayBinding.callUserName.text = item?.from?.fullName

                val image = Display_Image_URL + item?.from?.profileimage

                Glide.with(context)
                    .load(image)
                    .placeholder(R.drawable.ic_user)
                    .into(displayBinding.callUserProfile)


            }


            if (item?.isVideoCall == true){
                displayBinding.imgVideo.setImageResource(R.drawable.ic_video_calling);
            }else{
                displayBinding.imgVideo.setImageResource(R.drawable.ic_audio_calling);
            }



            displayBinding.txtCallTime.text = item?.callStartedAt?.let { getTimeInMillis(it) }

        }

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

}