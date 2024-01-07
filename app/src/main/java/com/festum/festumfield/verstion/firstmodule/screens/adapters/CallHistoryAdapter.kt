package com.festum.festumfield.verstion.firstmodule.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans.Display_Image_URL
import com.festum.festumfield.databinding.ItemCallHistoryBinding
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.CallHistoryInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.CallHistoryItem
import com.festum.festumfield.verstion.firstmodule.utils.DateTimeUtils
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class CallHistoryAdapter(
    private val context: Context,
    private var callHistoryItems : ArrayList<CallHistoryItem?>,
    private var callHistoryInterface : CallHistoryInterface
)
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

                if (item.to?.fullName.isNullOrEmpty()){
                    displayBinding.callUserName.text = item.to?.contactNo
                }else{
                    displayBinding.callUserName.text = item.to?.fullName
                }

                val image = Display_Image_URL + item.to?.profileimage

                Glide.with(context)
                    .load(image)
                    .placeholder(R.drawable.ic_user)
                    .into(displayBinding.callUserProfile)

            } else {

                if (item?.from?.fullName.isNullOrEmpty()){
                    displayBinding.callUserName.text = item?.from?.contactNo
                }else{
                    displayBinding.callUserName.text = item?.from?.fullName
                }

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


            displayBinding.imgVideo.setOnClickListener {

                callHistoryInterface.onCallFromHistory(callHistoryItems[position])

            }


            displayBinding.txtCallTime.text = getTimeInMillis(convertDateTimeToLong(item?.createdAt.toString()))

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
            now[Calendar.DATE] - smsTime[Calendar.DATE] in 2..6) {
            "" + DateFormat.format(dayFormatString, smsTime)
        } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
            DateFormat.format(dateTimeFormatString, smsTime).toString()
        } else {
            DateFormat.format("dd/MM/yy", smsTime).toString()
        }
    }

    fun String.convertToFormattedTime(): String {
        val inputFormat = SimpleDateFormat(DateTimeUtils.FORMAT_API_DATETIME, Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()

        val parsedDate = inputFormat.parse(this)

        return parsedDate?.let { outputFormat.format(it) } ?: ""
    }

    fun convertDateTimeToLong(dateTimeString: String): Long {

        /*val inputFormat = SimpleDateFormat(DateTimeUtils.FORMAT_API_DATETIME, Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")*/
        val formatter = DateTimeFormatter.ISO_INSTANT
        val instant = Instant.from(formatter.parse(dateTimeString))
        return instant.toEpochMilli()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterItems: ArrayList<CallHistoryItem?>) {
        callHistoryItems = filterItems
        notifyDataSetChanged()
    }

}