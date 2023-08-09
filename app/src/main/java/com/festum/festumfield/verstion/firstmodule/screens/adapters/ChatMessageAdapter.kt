package com.festum.festumfield.verstion.firstmodule.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.festum.festumfield.MyApplication
import com.festum.festumfield.R
import com.festum.festumfield.databinding.ItemSendProductBinding
import com.festum.festumfield.databinding.ItemSentImageBinding
import com.festum.festumfield.databinding.ItemSentMessageBinding
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ListItem
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ListSection
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.DocsItem

class ChatMessageAdapter(
    private val context: Context,
    private var listItems: ArrayList<ListItem>,
    private var receiverUserId : String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val MONTHHEADER = 0
    private val DATAVIEW = 1
    private val IMAGEVIEW = 2
    private val VIDEOVIEW = 3

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
    inner class ViewHolder1(private val messageBinding: ItemSentMessageBinding) : RecyclerView.ViewHolder(messageBinding.root) {

        fun bind(position: Int) {

            val item = listItems[position] as DocsItem


            if (item.from?.id.toString().uppercase() == MyApplication.getChannelId(MyApplication.context)){

                messageBinding.sendTxt.text = item.content?.text?.message
                messageBinding.rlLeft.visibility = View.GONE
                messageBinding.rlRight.visibility = View.VISIBLE

            } else {

                messageBinding.reciveTxt.text = item.content?.text?.message
                messageBinding.rlRight.visibility = View.GONE
                messageBinding.rlLeft.visibility = View.VISIBLE

            }

        }

    }
    inner class ViewHolder2(private val imageBinding: ItemSentImageBinding) : RecyclerView.ViewHolder(imageBinding.root) {

        fun bind(position: Int) {}

    }
    inner class ViewHolder3(private val productBinding: ItemSendProductBinding) : RecyclerView.ViewHolder(productBinding.root) {

        fun bind(position: Int) {}

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


}